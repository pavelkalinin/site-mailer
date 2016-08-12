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

    private static final Logger LOGGER = Logger.getLogger(Application.class);

    private static final int PRIVATE_PORTS_MINIMAL = 49152;
    private static final int PRIVATE_PORTS_MAXIMAL = 65535;

    private final Properties parameters;
    private boolean debug;
    private String handler;
    private int port;
    private Email to;
    private Email from;
    private Email admin;
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


    public Email emailTo() {
        return to;
    }


    public Email emailFrom() {
        return from;
    }


    public Email emailAdmin() {
        return admin;
    }


    public SMTPServer smtpServer() {
        return smtpServer;
    }


    public boolean isDebugMode() {
        return debug;
    }


    public Logger logger() {
        return LOGGER;
    }


    @Override
    public String toString() {
        return String.format("[" +
                        DEBUG_SERVICE + "=%b; " +
                        HANDLER + "=\"%s\"; " +
                        PORT + "=%d; " +
                        EMAIL_TO + "=\"%s\"; " +
                        EMAIL_FROM + "=\"%s\"; " +
                        EMAIL_ADMIN + "=\"%s\"; " +
                        "smtp.server=%s]",
                isDebugMode(),
                serviceHandler(),
                servicePort(),
                emailTo(),
                emailFrom(),
                emailAdmin(),
                smtpServer());
    }


    private void setup() {
        debug = readDebugService();
        LOGGER.setLevel(debug ? Level.DEBUG : Level.INFO);
        Logger.getRootLogger().setLevel(readDebugJetty() ? Level.DEBUG : Level.WARN);
        handler = readHandler();
        port = readPort();
        to = readEmailTo();
        from = readEmailFrom();
        admin = readEmailAdmin();
        smtpServer = new SMTPServer(new SMTPConfiguration(parameters));
        LOGGER.debug(String.format("Configuration %s has been loaded", toString()));
    }


    private String readHandler() {
        String property = HANDLER.property();
        return Validate.required(property, parameters.getProperty(property));
    }


    private int readPort() {
        String property = PORT.property();
        int port = Integer.parseInt(Validate.required(property, parameters.getProperty(property)));
        return Validate.isBetweenOrEquals("service port", port, PRIVATE_PORTS_MINIMAL, PRIVATE_PORTS_MAXIMAL);
    }


    private Email readEmailTo() {
        String property = EMAIL_TO.property();
        return Email.parse(Validate.required(property, parameters.getProperty(property)).trim());
    }


    private Email readEmailFrom() {
        return parseOrGetDefault(parameters.getProperty(EMAIL_FROM.property()));
    }


    private Email readEmailAdmin() {
        return parseOrGetDefault(parameters.getProperty(EMAIL_ADMIN.property()));
    }


    private Email parseOrGetDefault(final String email) {
        if (email == null) {
            LOGGER.warn("\'email.from\' property isn't defined - will be using the value of \'email.to\'");
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
