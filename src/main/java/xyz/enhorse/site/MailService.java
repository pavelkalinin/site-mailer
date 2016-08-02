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

        this.server = Validate.notNull("smtp server for mail service", configuration.smtpServer());
        this.recipient = recipient(Validate.notNull("recipient for mail service", configuration.recipient()));
        this.sender = Validate.notNullOrEmpty("sender for mail service", configuration.sender());
    }


    public void sendMail(final MailMessage mail) {
        Validate.notNull("mail message", mail);

        Session session = server.createSession();
        MimeMessage message = mimeMessage(session, mail);
        SMTPTransport transport = new SMTPTransport(session);

        transport.sendMessage(message);
    }


    private MimeMessage mimeMessage(final Session session, final MailMessage message) {
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            Address from = from(message);
            mimeMessage.setFrom(from);
            mimeMessage.setSubject(message.subject(), message.charset());
            mimeMessage.setText(content(message), message.charset());
            mimeMessage.setReplyTo(replyTo(message));
            mimeMessage.setHeader("X-Mailer", server.title());
            mimeMessage.addRecipient(Message.RecipientType.TO, recipient);
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
            return new InternetAddress(sender, message.name(), message.charset());
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Illegal sender address: \'" + sender + "\'", ex);
        }
    }


    private Address[] replyTo(final MailMessage message) {
        try {
            return new Address[]{new InternetAddress(message.email(), message.name(), message.charset())};
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Illegal \'From:\' address: \'" + message.email() + "\'", ex);
        }
    }


    private String content(final MailMessage message) {
        return String.format("%s%n%s<%s>", message.content(), message.name(), message.email());
    }
}
