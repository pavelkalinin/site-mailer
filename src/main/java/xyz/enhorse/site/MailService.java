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
    private final String emailTo;
    private final String emailFrom;


    public MailService(final Configuration configuration) {
        Validate.notNull("configuration", configuration);

        server = Validate.notNull("smtp server for mail service", configuration.smtpServer());
        emailTo = Validate.notNull("recipient of mail service", configuration.emailTo());
        emailFrom = Validate.notNullOrEmpty("sender of mail service", configuration.emailFrom());
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
            mimeMessage.setFrom(address(emailFrom, message));
            mimeMessage.setSubject(message.subject(), message.encoding());
            mimeMessage.setText(message.content(), message.encoding());
            mimeMessage.addRecipient(Message.RecipientType.TO, address(emailTo));
            mimeMessage.setSender(address(message));
            mimeMessage.setReplyTo(new Address[]{address(message)});
            mimeMessage.setHeader("X-Mailer", server.title());
            mimeMessage.saveChanges();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to generate a MIME message " +
                    "from message \'" + message + "\' " +
                    "to recipient \'" + emailTo + "\' " +
                    "by sender \'" + emailFrom + "\' " +
                    "during session + \'" + session + "\' ", ex);
        }

        return mimeMessage;
    }


    private Address address(final String address) {
        try {
            return new InternetAddress(address);
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Illegal address: \'" + address + "\'", ex);
        }
    }


    private Address address(final String address, final MailMessage message) {
        try {
            return new InternetAddress(address, message.name(), message.encoding());
        } catch (UnsupportedEncodingException ex) {
            return new InternetAddress();
        }
    }


    private Address address(final MailMessage message) {
        try {
            return new InternetAddress(message.address(), message.name(), message.encoding());
        } catch (UnsupportedEncodingException ex) {
            return new InternetAddress();
        }
    }
}
