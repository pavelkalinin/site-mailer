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
    private static final String RECIPIENT = "email.recipient";
    private static final String SENDER = "email.sender";
    private static final String ADMIN = "email.admin";
    private static final String REDIRECT_TO_SUCCESS = "redirect.success";
    private static final String REDIRECT_TO_FAIL = "redirect.fail";

    private static final String DEFAULT_ADMIN = "";
    private static final String DEFAULT_SENDER = "";
    private static final String DEFAULT_REDIRECT = "";

    private static final int PRIVATE_PORTS_MINIMAL = 49152;
    private static final int PRIVATE_PORTS_MAXIMAL = 65535;

    private final Properties parameters;
    private boolean debug;
    private String handler;
    private int port;
    private String recipient;
    private String sender;
    private String admin;
    private SMTPServer smtpServer;
    private String redirectSuccess;
    private String redirectFail;


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


    public String recipientAddress() {
        return recipient;
    }


    public String senderAddress() {
        return sender;
    }


    public String adminAddress() {
        return admin;
    }


    public SMTPServer smtpServer() {
        return smtpServer;
    }


    public String redirectToSuccess() {
        return redirectSuccess;
    }


    public String redirectToFail() {
        return redirectFail;
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
                        ADMIN + "=\"%s\"; " +
                        REDIRECT_TO_SUCCESS + "=\"%s\"; " +
                        REDIRECT_TO_FAIL + "=\"%s\"; " +
                        "smtp.server=%s]",
                isDebugMode(),
                serviceHandler(),
                servicePort(),
                recipientAddress(),
                senderAddress(),
                adminAddress(),
                redirectToSuccess(),
                redirectToFail(),
                smtpServer());
    }


    private void setup() {
        debug = readDebug();
        handler = readHandler();
        port = readPort();
        recipient = readRecipient();
        sender = readSender();
        admin = readAdmin();
        redirectSuccess = readRedirectSuccess();
        redirectFail = readRedirectFail();
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


    private String readRecipient() {
        String property = RECIPIENT;
        return Validate.required(property, parameters.getProperty(property));
    }


    private String readSender() {
        return Validate.defaultIfNull(parameters.getProperty(SENDER), DEFAULT_SENDER);
    }


    private String readAdmin() {
        return Validate.required(parameters.getProperty(ADMIN), DEFAULT_ADMIN);
    }


    private boolean readDebug() {
        return Boolean.parseBoolean(parameters.getProperty(DEBUG));
    }


    private String readRedirectSuccess() {
        return Validate.defaultIfNull(parameters.getProperty(REDIRECT_TO_SUCCESS), DEFAULT_REDIRECT);
    }


    private String readRedirectFail() {
        return Validate.defaultIfNull(parameters.getProperty(REDIRECT_TO_FAIL), DEFAULT_REDIRECT);
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
