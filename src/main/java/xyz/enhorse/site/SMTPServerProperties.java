package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;

import java.util.Properties;

import static xyz.enhorse.site.SMTPConfigurationProperties.*;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public class SMTPServerProperties {

    private final static int BASIC_PORT = 25;
    private final static int SECURE_PORT = 465;
    private final static int MSA_PORT = 587;

    private static final String PROTOCOL_PROPERTY = "mail.transport.protocol";

    private final static String DEFAULT_USER = "";
    private final static String DEFAULT_PASSWORD = "";
    private final static boolean DEFAULT_SSL = false;

    private final static boolean DEFAULT_TLS = false;

    private boolean debug;
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean authRequired;
    private boolean sslEnabled;
    private boolean tlsEnabled;
    private SMTPProtocols protocol;


    public SMTPServerProperties(final Properties properties, boolean debugMode) {
        setup(properties);
        debug = debugMode;
    }


    public String host() {
        return host;
    }


    public int port() {
        return port;
    }


    public String password() {
        return password;
    }


    public boolean isAuthRequired() {
        return authRequired;
    }


    public boolean isSSLEnabled() {
        return sslEnabled;
    }


    public boolean isTLSEnabled() {
        return tlsEnabled;
    }


    public String user() {
        return user;
    }


    boolean isDebugMode() {
        return debug;
    }


    public Properties get() {
        Properties properties = new Properties();
        properties.put(PROTOCOL_PROPERTY, protocol.definition());
        properties.put(HOST.translate(protocol), host);
        properties.put(PORT.translate(protocol), port);
        properties.put(USER.translate(protocol), user);
        properties.put(PASSWORD.translate(protocol), password);
        properties.put(AUTH.translate(protocol), authRequired);
        properties.put(SSL.translate(protocol), sslEnabled);
        properties.put(TLS.translate(protocol), tlsEnabled);

        return properties;
    }


    private void setup(final Properties properties) {
        host = readHost(properties);
        port = readPort(properties);
        user = readUser(properties);
        password = readPassword(properties);
        authRequired = readAuthorization(properties);
        sslEnabled = readSSL(properties);
        tlsEnabled = readTLS(properties);
        protocol = defineTransportProtocol(properties);
    }


    private String readHost(final Properties properties) {
        String property = HOST.property();
        return Validate.required(property, properties.getProperty(property));
    }


    private boolean readAuthorization(final Properties properties) {
        try {
            return Boolean.parseBoolean(properties.getProperty(AUTH.property()));
        } catch (Exception ex) {
            return !(readUser(properties).isEmpty() || readPassword(properties).isEmpty());
        }
    }


    private String readUser(final Properties properties) {
        return properties.getProperty(USER.property(), DEFAULT_USER);
    }


    private String readPassword(final Properties properties) {
        return properties.getProperty(PASSWORD.property(), DEFAULT_PASSWORD);
    }


    private boolean readSSL(final Properties properties) {
        try {
            return Boolean.parseBoolean(properties.getProperty(SSL.property()));
        } catch (Exception ex) {
            return DEFAULT_SSL;
        }
    }


    private boolean readTLS(final Properties properties) {
        try {
            return Boolean.parseBoolean(properties.getProperty(TLS.property()));
        } catch (Exception ex) {
            return DEFAULT_TLS;
        }
    }


    private int readPort(final Properties properties) {
        try {
            return Integer.parseInt(properties.getProperty(PORT.property()));
        } catch (Exception ex) {
            return definePort(properties);
        }

    }


    private int definePort(final Properties properties) {
        return (readTLS(properties)) ? MSA_PORT : ((readSSL(properties)) ? SECURE_PORT : BASIC_PORT);
    }


    private SMTPProtocols defineTransportProtocol(final Properties properties) {
        return (readSSL(properties) || readTLS(properties)) ? SMTPProtocols.SECURE : SMTPProtocols.BASIC;
    }
}
