package xyz.enhorse.site;

import xyz.enhorse.commons.HandyPath;
import xyz.enhorse.commons.Validate;
import xyz.enhorse.site.mail.SMTPConfiguration;
import xyz.enhorse.site.mail.SMTPServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.07.2016
 */
public class Configuration {

    private final static String DEBUG = "service.debug";
    private final static String HANDLER = "service.handler";
    private final static String PORT = "service.port";
    private final static String RECIPIENT = "recipient.email";

    private final static int PRIVATE_PORTS_MINIMAL = 49152;
    private final static int PRIVATE_PORTS_MAXIMAL = 65535;

    private final Properties parameters;
    private boolean debug;
    private String handler;
    private int port;
    private String recipient;
    private SMTPServer smtpServer;


    private Configuration(final Properties properties) {
        parameters = Validate.notNull("parameters", properties);
        setup();
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


    public boolean isDebugMode() {
        return debug;
    }


    @Override
    public String toString() {
        return String.format("[" +
                        DEBUG + "=%b; " +
                        HANDLER + "=\"%s\"; " +
                        PORT + "=%d; " +
                        RECIPIENT + "=\"%s\"; " +
                        "smtp.server=%s]",
                isDebugMode(),
                serviceHandler(),
                servicePort(),
                recipient(),
                smtpServer());
    }


    private void setup() {
        debug = readDebugMode();
        handler = readServiceHandler();
        port = readServicePort();
        recipient = readRecipientEmail();
        smtpServer = new SMTPServer(new SMTPConfiguration(parameters));
    }


    private String readServiceHandler() {
        String property = HANDLER;
        return Validate.required(property, parameters.getProperty(property));
    }


    private int readServicePort() {
        String property = PORT;
        int port = Integer.parseInt(Validate.required(property, parameters.getProperty(property)));
        return Validate.isBetweenOrEquals("service port", port, PRIVATE_PORTS_MINIMAL, PRIVATE_PORTS_MAXIMAL);
    }


    private String readRecipientEmail() {
        String property = RECIPIENT;
        return Validate.required(property, parameters.getProperty(property));
    }


    private boolean readDebugMode() {
        return Boolean.parseBoolean(parameters.getProperty(DEBUG));
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
