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


    public MailService(final SMTPServer server, final String recipient) {
        this.server = Validate.notNull("smtp server for mail service", server);
        this.recipient = convertToAddress(Validate.notNull("recipient for mail service", recipient));
    }


    public void sendMail(final MailMessage mail) {
        Session session = server.createSession();
        MimeMessage message = mimeMessage(session, mail);
        SMTPTransport transport = new SMTPTransport(session);

        transport.sendMessage(message);
    }


    private MimeMessage mimeMessage(final Session session, final MailMessage message) {
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom(convertToAddress(message.email(), message.from()));
            mimeMessage.setSender(convertToAddress(server.sender()));
            mimeMessage.addRecipient(Message.RecipientType.TO, recipient);
            mimeMessage.setSubject(message.subject(), server.charset());
            mimeMessage.setText(message.content(), server.charset());
            mimeMessage.saveChanges();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to generate a MIME message " +
                    "from the message \'" + message + "\' " +
                    "to the recipientAddress \'" + recipient + "\' " +
                    "during the session + \'" + session + "\' ", ex);
        }

        return mimeMessage;
    }


    private Address convertToAddress(final String address) {
        try {
            return new InternetAddress(address);
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Illegal address: \'" + address + "\'", ex);
        }
    }


    private Address convertToAddress(final String name, final String address) {
        try {
            return new InternetAddress(address, name, server.charset());
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Illegal address: \'" + address + "\'", ex);
        }
    }
}
