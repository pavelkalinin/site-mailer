package xyz.enhorse.site;

import xyz.enhorse.commons.PathEx;
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

    private static final String DEBUG = "service.debug";
    private static final String HANDLER = "service.handler";
    private static final String PORT = "service.port";
    private static final String EMAIL_TO = "email.to";
    private static final String EMAIL_FROM = "email.from";
    private static final String EMAIL_ADMIN = "email.admin";

    private static final int PRIVATE_PORTS_MINIMAL = 49152;
    private static final int PRIVATE_PORTS_MAXIMAL = 65535;

    private final Properties parameters;
    private boolean debug;
    private String handler;
    private int port;
    private String to;
    private String from;
    private String admin;
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


    public String emailTo() {
        return to;
    }


    public String emailFrom() {
        return from;
    }


    public String emailAdmin() {
        return admin;
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
        debug = readDebug();
        handler = readHandler();
        port = readPort();
        to = readEmailTo();
        from = readEmailFrom();
        admin = readEmailAdmin();
        smtpServer = new SMTPServer(new SMTPConfiguration(parameters));
    }


    private String readHandler() {
        String property = HANDLER;
        return Validate.required(property, parameters.getProperty(property));
    }


    private int readPort() {
        String property = PORT;
        int port = Integer.parseInt(Validate.required(property, parameters.getProperty(property)));
        return Validate.isBetweenOrEquals("service port", port, PRIVATE_PORTS_MINIMAL, PRIVATE_PORTS_MAXIMAL);
    }


    private String readEmailTo() {
        String property = EMAIL_TO;
        return Validate.required(property, parameters.getProperty(property));
    }


    private String readEmailFrom() {
        return Validate.defaultIfNull(parameters.getProperty(EMAIL_FROM), readEmailTo());
    }


    private String readEmailAdmin() {
        return Validate.defaultIfNull(parameters.getProperty(EMAIL_ADMIN), readEmailTo());
    }


    private boolean readDebug() {
        return Boolean.parseBoolean(parameters.getProperty(DEBUG));
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
