package xyz.enhorse.site;


import xyz.enhorse.commons.Validate;
import xyz.enhorse.site.mail.SMTPServer;
import xyz.enhorse.site.mail.SMTPTransport;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;


/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         27.07.2016
 */


public class MailService {

    private final SMTPServer server;
    private final Address recipient;
    private final String sender;


    public MailService(final Configuration configuration) {
        Validate.notNull("configuration", configuration);

        server = Validate.notNull("smtp server for mail service", configuration.smtpServer());
        recipient = recipient(Validate.notNull("recipient for mail service", configuration.recipientAddress()));
        sender = Validate.notNullOrEmpty("sender for mail service", configuration.senderAddress());
    }


    public void sendMail(final MailMessage mail) {
        Validate.notNull("mail message", mail);

        Session session = server.createSession();
        MimeMessage message = mimeMessage(session, mail);
        new SMTPTransport(session).sendMessage(message);
    }


    private MimeMessage mimeMessage(final Session session, final MailMessage message) {
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            Address from = from(message);
            mimeMessage.setFrom(from);
            mimeMessage.setSubject(message.subject(), message.encoding());
            mimeMessage.setText(message.content(), message.encoding());
            mimeMessage.addRecipient(Message.RecipientType.TO, recipient);
            mimeMessage.setReplyTo(replyTo(message));
            mimeMessage.setHeader("X-Mailer", server.title());
            mimeMessage.saveChanges();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to generate a MIME message " +
                    "from the message \'" + message + "\' " +
                    "to the recipient \'" + recipient + "\' " +
                    "by sender \'" + sender + "\' " +
                    "during the session + \'" + session + "\' ", ex);
        }

        return mimeMessage;
    }


    private Address recipient(final String address) {
        try {
            return new InternetAddress(address);
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Illegal recipient address: \'" + address + "\'", ex);
        }
    }


    private Address from(final MailMessage message) {
        try {
            return new InternetAddress(sender, message.name(), message.encoding());
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Illegal sender address: \'" + sender + "\'", ex);
        }
    }


    private Address[] replyTo(final MailMessage message) {
        try {
            return new Address[]{new InternetAddress(message.address(), message.name(), message.encoding())};
        } catch (UnsupportedEncodingException ex) {
            return new Address[]{};
        }
    }
}
