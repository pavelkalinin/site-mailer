package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;

import javax.mail.Authenticator;
import javax.mail.Session;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public class SMTPServer {

    private final SMTPServerProperties properties;


    public SMTPServer(final SMTPServerProperties properties) {
        this.properties = Validate.notNull("smtp server properties", properties);
    }


    public Session createSession() {
        Session session = Session.getInstance(properties.get(), authenticator());
        session.setDebug(properties.isDebugMode());

        return session;
    }


    private Authenticator authenticator() {
        return properties.isAuthRequired()
                ? (new SMTPAuthenticator(properties.user(), properties.password()))
                : null;
    }
}
