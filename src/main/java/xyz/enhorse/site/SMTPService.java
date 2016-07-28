package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;

import javax.mail.*;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public class SMTPService {

    private final Transport transport;


    public SMTPService(final Session session) {
        transport = get(Validate.notNull("session", session));
    }


    public void sendMessage(final Message message) {
        connect();
        send(message);
        close();
    }


    private void send(final Message message) {
        try {
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to send the message \'" + message + "\' " +
                    "using the transport \'" + transport + "\' ", ex);
        }
    }


    private void connect() {
        try {
            transport.connect();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to connect using the transport \'" + transport + "\'", ex);
        }
    }


    private void close() {
        try {
            transport.close();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to close the transport \'" + transport + "\'", ex);
        }
    }


    private Transport get(final Session session) {
        try {
            return session.getTransport();
        } catch (NoSuchProviderException ex) {
            System.out.println(ex.getMessage());
            throw new IllegalStateException("Failed to get a transport from the session \'" + session + "\'", ex);
        }
    }
}
