package xyz.enhorse.site;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import xyz.enhorse.commons.Email;
import xyz.enhorse.commons.PathEx;
import xyz.enhorse.commons.Validate;
import xyz.enhorse.site.mail.SMTPConfiguration;
import xyz.enhorse.site.mail.SMTPServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static xyz.enhorse.site.ServiceProperties.*;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.07.2016
 */
public class Configuration {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class);

    private static final int PRIVATE_PORTS_MINIMAL = 49152;
    private static final int PRIVATE_PORTS_MAXIMAL = 65535;
    private static final String CONTEXT_PATH_PREFIX = "/";

    private final Properties parameters;
    private final SMTPServer smtpServer;


    private Configuration(final Properties properties) {
        parameters = Validate.notNull("parameters", properties);
        setup();
        smtpServer = new SMTPServer(new SMTPConfiguration(parameters));
        LOGGER.info(String.format("Configuration %s has been successfully loaded", toString()));
    }


    public String serviceHandler() {
        return readHandler();
    }


    public int servicePort() {
        return readPort();
    }


    public Email emailTo() {
        return readEmailTo();
    }


    public Email emailFrom() {
        return readEmailFrom();
    }


    public Email emailAdmin() {
        return readEmailAdmin();
    }


    public SMTPServer smtpServer() {
        return smtpServer;
    }


    public Logger logger() {
        return LOGGER;
    }


    @Override
    public String toString() {
        return String.format("[" +
                        DEBUG_SERVICE + "=%b; " +
                        DEBUG_JETTY + "=%b; " +
                        HANDLER + "=\"%s\"; " +
                        PORT + "=%d; " +
                        EMAIL_TO + "=\"%s\"; " +
                        EMAIL_FROM + "=\"%s\"; " +
                        EMAIL_ADMIN + "=\"%s\"; " +
                        SMTP_SERVER + "=%s]",
                readDebugService(),
                readDebugJetty(),
                serviceHandler(),
                servicePort(),
                emailTo(),
                emailFrom(),
                emailAdmin(),
                smtpServer());
    }


    private void setup() {
        LOGGER.setLevel(readDebugService() ? Level.DEBUG : Level.INFO);
        Logger.getRootLogger().setLevel(readDebugJetty() ? Level.DEBUG : Level.WARN);
        checkRequirements();
    }


    private void checkRequirements() {
        try {
            readHandler();
            readPort();
            readEmailTo();
        } catch (IllegalStateException ex) {
            throw new IllegalArgumentException("Can't load configuration because the property " + ex.getMessage(), ex);
        }
    }


    private String readHandler() {
        String property = HANDLER.property();
        String handler = Validate.required(property, parameters.getProperty(property));

        if (!handler.startsWith(CONTEXT_PATH_PREFIX)) {
            handler = CONTEXT_PATH_PREFIX + handler;
        }

        return handler;
    }


    private int readPort() {
        String property = PORT.property();
        int port;
        try {
            port = Integer.parseInt(Validate.required(property, parameters.getProperty(property)));
            return Validate.isBetweenOrEquals(PORT.property(), port, PRIVATE_PORTS_MINIMAL, PRIVATE_PORTS_MAXIMAL);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(String.format("%s must be an integer number in range from %d to %d",
                    PORT.property(), PRIVATE_PORTS_MINIMAL, PRIVATE_PORTS_MAXIMAL));
        }
    }


    private Email readEmailTo() {
        String property = EMAIL_TO.property();
        return Email.parse(Validate.required(property, parameters.getProperty(property)).trim());
    }


    private Email readEmailFrom() {
        return parseOrGetDefault(EMAIL_FROM.property());
    }


    private Email readEmailAdmin() {
        return parseOrGetDefault(EMAIL_ADMIN.property());
    }


    private Email parseOrGetDefault(final String property) {
        String email = parameters.getProperty(property);

        if (email == null) {
            LOGGER.debug(String.format("\'%s\' property isn't defined - will be using the value of \'%s\'",
                    property, EMAIL_TO.property()));
            return readEmailTo();
        }
        return Email.parse(email.trim());
    }


    private boolean readDebugService() {
        return Boolean.parseBoolean(parameters.getProperty(DEBUG_SERVICE.property()));
    }


    private boolean readDebugJetty() {
        return Boolean.parseBoolean(parameters.getProperty(DEBUG_JETTY.property()));
    }


    public static Configuration loadFromFile(final String filename) {
        PathEx file = new PathEx(Validate.notNull("configuration file filename", filename));
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
