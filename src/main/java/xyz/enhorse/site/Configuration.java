package xyz.enhorse.site;

import xyz.enhorse.commons.HandyPath;
import xyz.enhorse.commons.Validate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.07.2016
 */
public class Configuration {

    private final static String SERVICE_HANDLER = "service.handler";
    private final static String SERVICE_PORT = "service.port";
    private final static int PRIVATE_PORTS_MINIMAL = 49152;
    private final static int PRIVATE_PORTS_MAXIMAL = 65535;

    private final static String MAIL_RECIPIENT = "mail.recipient";

    private final static String SMTP_SERVER = "smtp.server";
    private final static String SMTP_PORT = "smtp.port";
    private final static String SMTP_AUTH_REQUIRE = "smtp.auth.require";
    private final static String SMTP_SSL_ENABLE = "smtp.ssl.enable";
    private final static String SMTP_STARTTLS_ENABLE = "smtp.starttls.enable";
    private final static String SMTP_USERNAME = "smtp.username";
    private final static String SMTP_PASSWORD = "smtp.password";

    private final static boolean DEFAULT_SMTP_AUTH_REQUIRE = true;
    private final static boolean DEFAULT_SMTP_SSL_ENABLE = false;
    private final static boolean DEFAULT_SMTP_STARTTLS_ENABLE = false;
    private final static String DEFAULT_SMTP_USERNAME = "";
    private final static String DEFAULT_SMTP_PASSWORD = "";
    private final static int DEFAULT_SMTP_PORT = 25;
    private final static int DEFAULT_SECURE_SMTP_PORT = 465;
    private final static int DEFAULT_STARTTLS_SMTP_PORT = 587;


    private final Properties PARAMETERS;


    private Configuration(final Properties properties) {
        PARAMETERS = properties;
    }


    public String handler() {
        return PARAMETERS.getProperty(SERVICE_HANDLER);
    }


    public int port() {
        return Integer.parseInt(PARAMETERS.getProperty(SERVICE_PORT));
    }


    public Email recipient() {
        return Email.parse(PARAMETERS.getProperty(MAIL_RECIPIENT));
    }


    public String smtpServer() {
        return PARAMETERS.getProperty(SMTP_SERVER);
    }


    public int smtpPort() {
        return Integer.parseInt(PARAMETERS.getProperty(SMTP_PORT));
    }


    public boolean smtpAuthorizationRequire() {
        return Boolean.valueOf(PARAMETERS.getProperty(SMTP_AUTH_REQUIRE));
    }


    public boolean smtpSSLEnabled() {
        return Boolean.valueOf(PARAMETERS.getProperty(SMTP_SSL_ENABLE));
    }


    public boolean smtpSTARTSSLEnable() {
        return Boolean.valueOf(PARAMETERS.getProperty(SMTP_STARTTLS_ENABLE));
    }


    public String smtpUsername() {
        return PARAMETERS.getProperty(SMTP_USERNAME);
    }


    public String smtpPassword() {
        return PARAMETERS.getProperty(SMTP_PASSWORD);
    }


    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return PARAMETERS.toString();
    }


    public static Configuration loadFromFile(final String filename) {
        HandyPath file = new HandyPath(Validate.notNull("configuration file filename", filename));
        if (!file.isExistingFile()) {
            throw new IllegalArgumentException("Configuration file \'" + filename + "\' doesn't exist.");
        }

        Properties properties = buildDefault();
        try (FileInputStream stream = new FileInputStream(file.toFile())) {
            properties.load(stream);
        } catch (IOException ex) {
            throw new IllegalStateException("Error loading the configuration file \'" + filename + "\'.");
        }

        return load(properties);
    }


    private static Configuration load(final Properties properties) {
        return new Configuration(checkParameters(properties));
    }


    private static Properties checkParameters(Properties properties) {
        checkServiceHandler(properties);
        checkServicePort(properties);
        checkMailRecipient(properties);
        checkSMTPServer(properties);
        checkSMTPAuthority(properties);
        checkSMTPPort(properties);

        return properties;
    }


    private static void checkMailRecipient(final Properties properties) {
        String recipient = Validate.required(MAIL_RECIPIENT, properties.getProperty(MAIL_RECIPIENT));

        if (Email.parse(recipient) == null) {
            throw new IllegalArgumentException("\'" + MAIL_RECIPIENT + "=" + recipient
                    + "\' doesn't corresponded to a correct email address.");
        }
    }


    private static void checkServiceHandler(final Properties properties) {
        Validate.required(SERVICE_HANDLER, properties.getProperty(SERVICE_HANDLER));
    }


    private static void checkServicePort(final Properties properties) {
        int servicePort = Integer.parseInt(Validate.notNullOrEmpty(SERVICE_PORT, properties.getProperty(SERVICE_PORT)));
        Validate.isGreaterOrEqual(SERVICE_PORT, servicePort, PRIVATE_PORTS_MINIMAL);
        Validate.isLessOrEqual(SERVICE_PORT, servicePort, PRIVATE_PORTS_MAXIMAL);
    }


    private static void checkSMTPServer(final Properties properties) {
        Validate.required(SMTP_SERVER, properties.getProperty(SMTP_SERVER));
    }


    private static void checkSMTPAuthority(final Properties properties) {
        boolean authRequired = Boolean.valueOf(properties.getProperty(SMTP_AUTH_REQUIRE));

        if (authRequired && (properties.getProperty(SMTP_USERNAME).isEmpty())) {
            throw new IllegalStateException("\'" + SMTP_AUTH_REQUIRE + "\' is set to TRUE, but \'"
                    + SMTP_USERNAME + "\' was not defined.");
        }
    }


    private static void checkSMTPPort(Properties properties) {
        int port = DEFAULT_SMTP_PORT;

        if (properties.containsKey(SMTP_PORT)) {
            port = Integer.parseInt(properties.getProperty(SMTP_PORT));
        } else {
            if (Boolean.valueOf(properties.getProperty(SMTP_SSL_ENABLE))) {
                port = (Boolean.valueOf(properties.getProperty(SMTP_STARTTLS_ENABLE)))
                        ? DEFAULT_STARTTLS_SMTP_PORT
                        : DEFAULT_SECURE_SMTP_PORT;
            }
        }

        properties.put(SMTP_PORT, port);
    }


    private static Properties buildDefault() {
        Properties properties = new Properties();
        properties.put(SMTP_AUTH_REQUIRE, DEFAULT_SMTP_AUTH_REQUIRE);
        properties.put(SMTP_SSL_ENABLE, DEFAULT_SMTP_SSL_ENABLE);
        properties.put(SMTP_STARTTLS_ENABLE, DEFAULT_SMTP_STARTTLS_ENABLE);
        properties.put(SMTP_USERNAME, DEFAULT_SMTP_USERNAME);
        properties.put(SMTP_PASSWORD, DEFAULT_SMTP_PASSWORD);
        return properties;
    }
}
