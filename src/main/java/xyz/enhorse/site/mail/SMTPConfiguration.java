package xyz.enhorse.site.mail;

import xyz.enhorse.commons.Validate;

import java.nio.charset.Charset;
import java.util.Properties;

import static xyz.enhorse.site.mail.SMTPProperties.*;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public class SMTPConfiguration {

    private final static int BASIC_PORT = 25;
    private final static int SECURE_PORT = 465;
    private final static int MSA_PORT = 587;

    private final static String DEFAULT_USER = "";
    private final static String DEFAULT_PASSWORD = "";
    private final static boolean DEFAULT_SSL = false;
    private final static boolean DEFAULT_TLS = false;


    private final Properties parameters;

    private String host;
    private int port;
    private String sender;
    private String user;
    private String password;
    private boolean authRequired;
    private boolean sslEnabled;
    private boolean tlsEnabled;
    private SMTPProtocols protocol;
    private Charset charset;
    private boolean debug;


    public SMTPConfiguration(final Properties properties) {
        this.parameters = Validate.notNull("properties for smtp server", properties);
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


    public String sender() {
        return sender;
    }


    public String charset() {
        return charset.name();
    }


    boolean isDebugMode() {
        return debug;
    }


    public Properties get() {
        Properties properties = new Properties(parameters);

        properties.put(HOST.forProtocol(protocol), host);
        properties.put(PORT.forProtocol(protocol), port);
        properties.put(USER.forProtocol(protocol), user);
        properties.put(PASSWORD.forProtocol(protocol), password);
        properties.put(AUTH.forProtocol(protocol), authRequired);
        properties.put(SSL.forProtocol(protocol), sslEnabled);
        properties.put(TLS.forProtocol(protocol), tlsEnabled);
        properties.put(CHARSET.forProtocol(protocol), charset);
        properties.put(PROTOCOL.forProtocol(protocol), protocol.tag());
        properties.put(DEBUG.forProtocol(protocol), debug);

        //TODO fix strange behaviour of 'mail.debug' property

        return properties;
    }


    private void setup() {
        host = readHost();
        port = readPort();
        sender = readSender();
        user = readUser();
        password = readPassword();
        authRequired = readAuthorization();
        sslEnabled = readSSL();
        tlsEnabled = readTLS();
        charset = readCharset();
        protocol = defineProtocol();
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
            return definePort();
        }

    }


    private int definePort() {
        return (readTLS()) ? MSA_PORT : ((readSSL()) ? SECURE_PORT : BASIC_PORT);
    }


    private String readSender() {
        return parameters.getProperty(SENDER.property(), readUser());
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


    private Charset readCharset() {
        try {
            return Charset.forName(parameters.getProperty(CHARSET.property()));
        } catch (Exception ex) {
            return Charset.defaultCharset();
        }
    }


    private SMTPProtocols defineProtocol() {
        return (readSSL() || readTLS()) ? SMTPProtocols.SECURE : SMTPProtocols.BASIC;
    }


    private boolean readDebug() {
        return Boolean.parseBoolean(parameters.getProperty(DEBUG.property()));
    }
}
