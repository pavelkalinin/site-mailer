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

    private final static String SERVER = "smtp.server";
    private final static String AUTH_REQUIRED = "smtp.auth.required";
    private final static String USE_SECURE = "smtp.use.secure";
    private final static String USERNAME = "smtp.username";
    private final static String PASSWORD = "smtp.password";

    private final static boolean DEFAULT_AUTH_REQUIRED = false;
    private final static boolean DEFAULT_USE_SECURE = true;
    private final static String DEFAULT_USERNAME = "";
    private final static String DEFAULT_PASSWORD = "";

    private final static int PRIVATE_PORTS_MINIMAL = 49152;
    private final static int PRIVATE_PORTS_MAXIMAL = 65535;


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


    public String smtpServer() {
        return PARAMETERS.getProperty(SERVER);
    }


    public boolean smtpAuthorizationRequired() {
        return Boolean.valueOf(PARAMETERS.getProperty(AUTH_REQUIRED));
    }


    public boolean smtpUseSecure() {
        return Boolean.valueOf(PARAMETERS.getProperty(USE_SECURE));
    }


    public String smtpUsername() {
        return PARAMETERS.getProperty(USERNAME);
    }


    public String smtpPassword() {
        return PARAMETERS.getProperty(PASSWORD);
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


    private static Properties checkParameters(final Properties properties) {
        Validate.required(SERVICE_HANDLER, properties.getProperty(SERVICE_HANDLER));

        int port = Integer.parseInt(Validate.notNullOrEmpty(SERVICE_PORT, properties.getProperty(SERVICE_PORT)));
        port = Validate.isGreaterOrEqual(SERVICE_PORT, port, PRIVATE_PORTS_MINIMAL);
        Validate.isLessOrEqual(SERVICE_PORT, port, PRIVATE_PORTS_MAXIMAL);

        Validate.required(SERVER, properties.getProperty(SERVER));

        String username = properties.getProperty(USERNAME);
        boolean authRequired = Boolean.valueOf(properties.getProperty(AUTH_REQUIRED));
        if (authRequired && username.isEmpty()) {
            throw new IllegalStateException("\'" + AUTH_REQUIRED + "\' is set to TRUE, but \'"
                    + USERNAME + "\' was not defined.");
        }


        return properties;
    }


    private static Properties buildDefault() {
        Properties properties = new Properties();
        properties.put(AUTH_REQUIRED, DEFAULT_AUTH_REQUIRED);
        properties.put(USE_SECURE, DEFAULT_USE_SECURE);
        properties.put(USERNAME, DEFAULT_USERNAME);
        properties.put(PASSWORD, DEFAULT_PASSWORD);
        return properties;
    }
}
