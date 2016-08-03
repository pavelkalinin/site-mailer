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
    private static final String RECIPIENT = "recipient.email";
    private static final String SENDER = "sender.email";
    private static final String REDIRECT_TO_SUCCESS = "redirect.to.success";
    private static final String REDIRECT_TO_FAIL = "redirect.to.fail";

    private static final int PRIVATE_PORTS_MINIMAL = 49152;
    private static final int PRIVATE_PORTS_MAXIMAL = 65535;

    private static final String DEFAULT_REDIRECT_TO = "";

    private final Properties parameters;
    private boolean debug;
    private String handler;
    private int port;
    private String recipient;
    private String sender;
    private SMTPServer smtpServer;
    private String redirectToSuccess;
    private String redirectToFail;


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


    public String sender() {
        return sender;
    }


    public SMTPServer smtpServer() {
        return smtpServer;
    }


    public String redirectToSuccess() {
        return redirectToSuccess;
    }


    public String redirectToFail() {
        return redirectToFail;
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
                        SENDER + "=\"%s\"; " +
                        REDIRECT_TO_SUCCESS + "=\"%s\"; " +
                        REDIRECT_TO_FAIL + "=\"%s\"; " +
                        "smtp.server=%s]",
                isDebugMode(),
                serviceHandler(),
                servicePort(),
                recipient(),
                sender(),
                redirectToSuccess(),
                redirectToFail(),
                smtpServer());
    }


    private void setup() {
        debug = readDebugMode();
        handler = readServiceHandler();
        port = readServicePort();
        recipient = readRecipientEmail();
        sender = readSenderEmail();
        redirectToSuccess = readRedirectToSuccess();
        redirectToFail = readRedirectToFail();
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


    private String readSenderEmail() {
        String property = SENDER;
        return Validate.required(property, parameters.getProperty(property));
    }


    private boolean readDebugMode() {
        return Boolean.parseBoolean(parameters.getProperty(DEBUG));
    }


    private String readRedirectToSuccess() {
        return Validate.defaultIfNull(parameters.getProperty(REDIRECT_TO_SUCCESS), DEFAULT_REDIRECT_TO);
    }


    private String readRedirectToFail() {
        return Validate.defaultIfNull(parameters.getProperty(REDIRECT_TO_FAIL), DEFAULT_REDIRECT_TO);
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
