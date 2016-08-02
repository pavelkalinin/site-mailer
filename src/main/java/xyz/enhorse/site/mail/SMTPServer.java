package xyz.enhorse.site.mail;

import xyz.enhorse.commons.Validate;

import javax.mail.Authenticator;
import javax.mail.Session;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public class SMTPServer {

    private final SMTPConfiguration configuration;


    public SMTPServer(final SMTPConfiguration configuration) {
        this.configuration = Validate.notNull("smtp server configuration", configuration);
    }


    public String sender() {
        return configuration.sender();
    }


    public Session createSession() {
        return Session.getInstance(configuration.get(), authenticator());
    }


    public String title() {
        return configuration.mailer();
    }


    private Authenticator authenticator() {
        return configuration.isAuthRequired()
                ? (new SMTPAuthenticator(configuration.user(), configuration.password()))
                : null;
    }


    @Override
    public String toString() {
        return configuration.toString();
    }
}
