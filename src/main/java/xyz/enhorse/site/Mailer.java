package xyz.enhorse.site;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         27.07.2016
 */


public class Mailer {

    private final Properties mailServerProperties;
    private final Authenticator authenticator;
    private final InternetAddress recipient;


    public Mailer(final Configuration configuration) {
        mailServerProperties = setupServer(configuration);
        authenticator = setupAuthority(configuration);
        recipient = configuration.recipientAddress();
    }


    private Authenticator setupAuthority(final Configuration configuration) {
        return new SMTPAuthenticator(configuration.smtpUsername(), configuration.smtpPassword());
    }


    private Properties setupServer(final Configuration configuration) {
        Properties properties = System.getProperties();
        String prefix = "mail" + configuration.smtpTransportProtocol();

        properties.put(prefix + ".host", configuration.smtpHost());
        properties.put(prefix + ".servicePort", configuration.smtpPort());
        properties.put(prefix + ".auth", configuration.smtpAuthorizationRequired());
        properties.put(prefix + ".starttls.enable", configuration.smtpTLSEnabled());

        properties.put("mail.transport.protocol", configuration.smtpTransportProtocol());

        System.out.println(properties);
        //Mail Server Properties have been setupServer successfully.."
        return properties;
    }


    public void sendMessage(final MailMessage message) throws Exception {
        Session session = createSession(authenticator);
        MimeMessage mail = generateMimeMessage(session, message);
        Transport transport = session.getTransport();

        try {
            transport.connect();
            transport.sendMessage(mail, mail.getAllRecipients());
        } catch (MessagingException ex) {
            transport.close();
            throw new IllegalStateException("Failed to send the mail \'" + mail + "\' " +
                    "via \'" + transport + "\' " +
                    "during the session \'" + session + "\'");
        }
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
                    "during the session + \'" + session + "\' ");
        }

        return mimeMessage;
    }


    private Address convertToAddress(final String sender) {
        try {
            return new InternetAddress(sender);
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Illegal sender address: \'" + sender + "\'");
        }
    }


    private Session createSession(final Authenticator authenticator) {
        return Session.getDefaultInstance(mailServerProperties, authenticator);
    }


    public String recipient() {
        return recipient.getAddress();
    }


    private static class SMTPAuthenticator extends javax.mail.Authenticator {

        private String username;
        private String password;


        public SMTPAuthenticator(final String username, final String password) {
            this.username = username;
            this.password = password;
        }


        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
}
