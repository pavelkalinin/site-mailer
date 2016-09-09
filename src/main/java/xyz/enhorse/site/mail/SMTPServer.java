package xyz.enhorse.site.mail;

import xyz.enhorse.commons.parameters.Parameter;
import xyz.enhorse.commons.parameters.Parameters;

import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Map;
import java.util.Properties;

import static xyz.enhorse.site.mail.SMTPProperties.*;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public class SMTPServer {

    private final Parameters configuration;


    public SMTPServer(final Map<String, String> properties) {
        this.configuration = load(properties);
    }


    public Session createSession() {
        return Session.getInstance(toProperties(), authenticator());
    }


    private Properties toProperties() {
        Properties properties = new Properties();

        for (String s : configuration) {
            properties.put(s, String.valueOf(configuration.get(s)));
        }

        return properties;
    }


    public String title() {
        return (String) configuration.get(MAILER.property()).value();
    }


    private Authenticator authenticator() {
        return isAuthRequired()
                ? (new SMTPAuthenticator(user(), password()))
                : null;
    }


    private String password() {
        return (String) configuration.get(PASSWORD.property()).value();
    }


    private String user() {
        return (String) configuration.get(USER.property()).value();
    }


    private boolean isAuthRequired() {
        return (Boolean) configuration.get(AUTH.property()).value();
    }


    private Parameters load(final Map<String, String> properties) {
        try {
            Parameters parameters = SMTPProperties.schema().process(properties);
            SMTPProtocols protocol = (isSSL() || isTLS()) ? SMTPProtocols.SECURE : SMTPProtocols.BASIC;
            parameters.replace(new Parameter<>(PROTOCOL.property(), protocol));
            return defineAbsent(parameters);

        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Error loading the configuration file \'" + properties + "\'.");
        }
    }


    private Parameters defineAbsent(final Parameters parameters) {
        if (parameters.get(PORT.property()).value() == null) {
            parameters.put(new Parameter<>(PORT.property(), getProtocol().port()));
        }

        return parameters;
    }


    private boolean isSSL() {
        return (Boolean) configuration.get(SSL.property()).value();
    }


    private boolean isTLS() {
        return (Boolean) configuration.get(TLS.property()).value();
    }


    private SMTPProtocols getProtocol() {
        return (SMTPProtocols) configuration.get(PROTOCOL.property()).value();
    }


    @Override
    public String toString() {
        return configuration.toString();
    }
}
