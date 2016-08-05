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
    private final String from;


    public MailService(final SMTPServer smtpServer, final String emailFrom) {
        server = Validate.notNull("smtp server for mail service", smtpServer);
        from = Validate.notNullOrEmpty("sender's email", emailFrom);
    }


    public String title() {
        return server.title();
    }


    public void sendMail(final String to, final MailMessage mail) {
        Validate.notNull("mail message", mail);

        Session session = server.createSession();
        MimeMessage message = mimeMessage(to, mail, session);
        new SMTPTransport(session).sendMessage(message);
    }


    private MimeMessage mimeMessage(final String to, final MailMessage message, final Session session) {
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom(address(from, message));
            mimeMessage.setSubject(message.subject(), message.encoding());
            mimeMessage.setText(message.content(), message.encoding());
            mimeMessage.addRecipient(Message.RecipientType.TO, address(to));
            mimeMessage.setSender(address(message));
            mimeMessage.setReplyTo(new Address[]{address(message)});
            mimeMessage.setHeader("X-Mailer", server.title());
            mimeMessage.saveChanges();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to generate a MIME message " +
                    "from message \'" + message + "\' " +
                    "to recipient \'" + to + "\' " +
                    "by sender \'" + from + "\' " +
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
