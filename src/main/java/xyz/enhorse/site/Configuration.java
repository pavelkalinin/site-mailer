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

    private final static String DEBUG = "debug";
    private final static String HANDLER = "service.handler";
    private final static String PORT = "service.port";
    private final static String RECIPIENT = "recipient.email";

    private final static boolean DEFAULT_DEBUG_MODE = false;

    private final static int PRIVATE_PORTS_MINIMAL = 49152;
    private final static int PRIVATE_PORTS_MAXIMAL = 65535;

    private final Properties PARAMETERS;

    private boolean debug;
    private String handler;
    private int port;
    private String recipient;
    private SMTPServer smtpServer;


    private Configuration(final Properties properties) {
        PARAMETERS = setup(properties);
    }


    public String serviceHandler() {
        return handler;
    }


    public int servicePort() {
        return port;
    }


    public String recipient() {
        return recipient;
    }


    public SMTPServer smtpServer() {
        return smtpServer;
    }


    public Properties rawProperties() {
        return PARAMETERS;
    }


    boolean isDebugMode() {
        return debug;
    }


    @Override
    public String toString() {
        return "Configuration{" +
                "debug=" + isDebugMode() +
                ", handler=\'" + serviceHandler() + '\'' +
                ", port=" + servicePort() +
                ", recipient=\'" + recipient() + '\'' +
                ", smtpServer=" + smtpServer() +
                '}';
    }


    private Properties setup(final Properties properties) {
        debug = readDebugMode(properties);
        handler = readServiceHandler(properties);
        port = readServicePort(properties);
        recipient = readRecipientEmail(properties);
        smtpServer = new SMTPServer(new SMTPServerProperties(properties, debug));

        return properties;
    }


    private boolean readDebugMode(final Properties properties) {
        boolean debug;

        try {
            debug = Boolean.valueOf(properties.getProperty(DEBUG));
        } catch (Exception ex) {
            debug = DEFAULT_DEBUG_MODE;
        }

        return debug;
    }


    private String readServiceHandler(final Properties properties) {
        String property = HANDLER;
        return Validate.required(property, properties.getProperty(property));
    }


    private int readServicePort(final Properties properties) {
        String property = PORT;
        int port = Integer.parseInt(Validate.required(property, properties.getProperty(property)));
        return Validate.isBetweenOrEquals("service port", port, PRIVATE_PORTS_MINIMAL, PRIVATE_PORTS_MAXIMAL);
    }


    private String readRecipientEmail(final Properties properties) {
        String property = RECIPIENT;
        return Validate.required(property, properties.getProperty(property));
    }


    public static Configuration loadFromFile(final String filename) {
        HandyPath file = new HandyPath(Validate.notNull("configuration file filename", filename));
        if (!file.isExistingFile()) {
            throw new IllegalArgumentException("Configuration file \'" + filename + "\' doesn't exist.");
        }

        Properties properties = new Properties();
        try (FileInputStream stream = new FileInputStream(file.toFile())) {
            properties.load(stream);
        } catch (IOException ex) {
            throw new IllegalStateException("Error loading the configuration file \'" + filename + "\'.");
        }

        return new Configuration(properties);
    }
}
