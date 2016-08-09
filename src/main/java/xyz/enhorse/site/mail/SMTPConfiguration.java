package xyz.enhorse.site.mail;

import xyz.enhorse.commons.Pretty;
import xyz.enhorse.commons.Validate;

import java.util.Properties;

import static xyz.enhorse.site.mail.SMTPProperties.*;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public class SMTPConfiguration {

    private final static String DEFAULT_USER = "";
    private final static String DEFAULT_PASSWORD = "";
    private final static String DEFAULT_MAILER = "";
    private final static boolean DEFAULT_SSL = false;
    private final static boolean DEFAULT_TLS = false;


    private final Properties parameters;

    private String host;
    private int port;
    private String user;
    private String password;
    private boolean authRequired;
    private boolean sslEnabled;
    private boolean tlsEnabled;
    private SMTPProtocols protocol;
    private String mailer;
    private boolean debug;


    public SMTPConfiguration(final Properties properties) {
        parameters = Validate.notNull("properties for smtp server", properties);
        setup();
    }


    public boolean isAuthRequired() {
        return authRequired;
    }


    public String user() {
        return user;
    }


    public String password() {
        return password;
    }


    public String mailer() {
        return mailer;
    }


    public Properties get() {
        Properties properties = new Properties();

        properties.put(HOST.of(protocol), host);
        properties.put(PORT.of(protocol), port);
        properties.put(USER.of(protocol), user);
        properties.put(PASSWORD.of(protocol), password);
        properties.put(AUTH.of(protocol), authRequired);
        properties.put(SSL.of(protocol), sslEnabled);
        properties.put(TLS.of(protocol), tlsEnabled);
        properties.put(PROTOCOL.of(protocol), protocol.tag());
        properties.put(MAILER.of(protocol), mailer);
        properties.put(DEBUG.of(protocol), String.valueOf(debug)); // javax.mail.Session counts on to find String

        return properties;
    }


    private void setup() {
        host = readHost();
        port = readPort();
        user = readUser();
        password = readPassword();
        authRequired = readAuthorization();
        sslEnabled = readSSL();
        tlsEnabled = readTLS();
        protocol = defineProtocol();
        mailer = readMailer();
        debug = readDebug();
    }


    private String readHost() {
        String property = HOST.property();
        return Validate.required(property, parameters.getProperty(property));
    }


    private int readPort() {
        try {
            return Integer.parseInt(parameters.getProperty(PORT.property()));
        } catch (Exception ex) {
            return defineProtocol().port();
        }

    }


    private String readUser() {
        return parameters.getProperty(USER.property(), DEFAULT_USER);
    }


    private String readPassword() {
        return parameters.getProperty(PASSWORD.property(), DEFAULT_PASSWORD);
    }


    private boolean readAuthorization() {
        try {
            return Boolean.parseBoolean(parameters.getProperty(AUTH.property()));
        } catch (Exception ex) {
            return !(readUser().isEmpty() || readPassword().isEmpty());
        }
    }


    private boolean readSSL() {
        try {
            return Boolean.parseBoolean(parameters.getProperty(SSL.property()));
        } catch (Exception ex) {
            return DEFAULT_SSL;
        }
    }


    private boolean readTLS() {
        try {
            return Boolean.parseBoolean(parameters.getProperty(TLS.property()));
        } catch (Exception ex) {
            return DEFAULT_TLS;
        }
    }


    private SMTPProtocols defineProtocol() {
        return (readSSL() || readTLS()) ? SMTPProtocols.SECURE : SMTPProtocols.BASIC;
    }


    private String readMailer() {
        return parameters.getProperty(MAILER.property(), DEFAULT_MAILER);
    }


    private boolean readDebug() {
        return Boolean.parseBoolean(parameters.getProperty(DEBUG.property()));
    }


    @Override
    public String toString() {
        return Pretty.format(get());
    }
}
