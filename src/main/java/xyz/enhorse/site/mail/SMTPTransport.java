package xyz.enhorse.site.mail;

import xyz.enhorse.commons.Validate;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public class SMTPTransport {

    private final Transport transport;


    public SMTPTransport(final Session session) {
        transport = get(Validate.notNull("session for smtp service", session));
    }


    public void sendMessage(final Message message) {
        connect();
        send(message);
        close();
    }


    private Transport get(final Session session) {
        try {
            return session.getTransport();
        } catch (NoSuchProviderException ex) {
            throw new IllegalStateException("Failed to get a transport", ex);
        }
    }


    private void connect() {
        try {
            transport.connect();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to connect to a service", ex);
        }
    }


    private void send(final Message message) {
        try {
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to send the message \'" + message + "\' ", ex);
        }
    }


    private void close() {
        try {
            transport.close();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to close a transport", ex);
        }
    }
}
