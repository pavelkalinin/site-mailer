package xyz.enhorse.site.mail;

import xyz.enhorse.commons.Validate;

import javax.mail.*;
import java.util.Arrays;
import java.util.Objects;

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
            throw new IllegalStateException("Failed to send the message \'" + messageToString(message) + "\' ", ex);
        }
    }


    private void close() {
        try {
            transport.close();
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to close a transport", ex);
        }
    }


    private static String messageToString(final Message message) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append("from:").append(Arrays.deepToString(message.getFrom())).append("\';")
                    .append("subject:\'").append(message.getSubject()).append("\';")
                    .append("content:").append(Objects.toString(message.getContent()));
        } catch (Exception ex) {
            builder.append((builder.length() == 0)
                    ? String.format("Can't convert message%n")
                    : String.format("%n...failed to convert message with the error: \'"));
            builder.append(ex.getMessage()).append('\'');
        }
        return builder.toString();
    }

}
