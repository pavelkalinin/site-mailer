package xyz.enhorse.site;


import xyz.enhorse.commons.Validate;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         27.07.2016
 */


public class Mailer {

    private final SMTPServer server;
    private final Address recipient;


    public Mailer(final SMTPServer server, final String recipient) {
        this.server = Validate.notNull("smtp server", server);
        this.recipient = convertToAddress(Validate.notNull("recipient", recipient));
    }


    public void sendMail(final MailMessage mail) {
        Session session = server.createSession();
        MimeMessage message = generateMimeMessage(session, mail);
        SMTPService transport = new SMTPService(session);

        transport.sendMessage(message);
    }


    private MimeMessage generateMimeMessage(final Session session, final MailMessage message) {
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setSender(convertToAddress(message.sender()));
            mimeMessage.addRecipient(Message.RecipientType.TO, recipient);
            mimeMessage.setSubject(message.subject());
            mimeMessage.setContent(message.message(), "text/html");
            mimeMessage.saveChanges();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to generate a MIME message " +
                    "from the message \'" + message + "\' " +
                    "to the recipientAddress \'" + recipient + "\' " +
                    "during the session + \'" + session + "\' ", ex);
        }

        return mimeMessage;
    }


    private static Address convertToAddress(final String address) {
        try {
            return new InternetAddress(address);
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Illegal address: \'" + address + "\'", ex);
        }
    }
}
