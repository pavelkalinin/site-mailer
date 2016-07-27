package xyz.enhorse.site;

import xyz.enhorse.commons.HandyPath;
import xyz.enhorse.commons.Validate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.07.2016
 */
public class Configuration {

    //configuration file parameters
    private final static String DEBUG_MODE = "debug";
    private final static String SERVICE_HANDLER = "service.handler";

    private final static String SERVICE_PORT = "service.port";

    private final static String RECIPIENT_ADDRESS = "recipient.email";
    private final static String SMTP_HOST = "smtp.host";
    private final static String SMTP_USERNAME = "smtp.username";
    private final static String SMTP_PASSWORD = "smtp.password";
    private final static String SMTP_SSL = "smtp.ssl.enable";
    private final static String SMTP_TLS = "smtp.tls.enable";


    //defaults if absent
    private final static boolean DEFAULT_DEBUG_MODE = false;
    private final static boolean DEFAULT_SMTP_SSL = false;
    private final static boolean DEFAULT_SMTP_TLS = false;
    private final static String DEFAULT_SMTP_USERNAME = "";
    private final static String DEFAULT_SMTP_PASSWORD = "";


    //RFC constants
    private final static int BASIC_SMTP_PORT = 25;
    private final static int SECURE_SMTP_PORT = 465;
    private final static int TLS_SMTP_PORT = 587;

    private final static String SMTP_BASIC_PROTOCOL = "smtp";
    private final static String SMTP_SECURE_PROTOCOL = "smtps";

    private final static int PRIVATE_PORTS_MINIMAL = 49152;
    private final static int PRIVATE_PORTS_MAXIMAL = 65535;


    private final Properties PARAMETERS;

    private String serviceHandler;
    private int servicePort;
    private InternetAddress recipientAddress;
    private String smtpHost;
    private int smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private boolean smtpAuthRequired;
    private boolean smtpSSLEnabled;
    private boolean smtpTLSEnabled;
    private String smtpTransportProtocol;
    private boolean debugMode;


    private Configuration(final Properties properties) {
        PARAMETERS = setup(properties);
    }


    public String serviceHandler() {
        return serviceHandler;
    }


    public int servicePort() {
        return servicePort;
    }


    public InternetAddress recipientAddress() {
        return recipientAddress;
    }


    public String smtpHost() {
        return smtpHost;
    }


    public int smtpPort() {
        return smtpPort;
    }


    public boolean smtpAuthorizationRequired() {
        return smtpAuthRequired;
    }


    public boolean smtpSSLEnabled() {
        return smtpSSLEnabled;
    }


    public boolean smtpTLSEnabled() {
        return smtpTLSEnabled;
    }


    public String smtpUsername() {
        return smtpUsername;
    }


    public String smtpPassword() {
        return smtpPassword;
    }


    public String smtpTransportProtocol() {
        return smtpTransportProtocol;
    }


    public Properties rawProperties() {
        return PARAMETERS;
    }


    boolean isDebugMode() {
        return debugMode;
    }


    void print() {
        System.out.println("service handler=\'" + serviceHandler() + "\'");
        System.out.println("service port=" + servicePort());
        System.out.println("recipient address=\'" + recipientAddress() + "\'");
        System.out.println("SMTP host=\'" + smtpHost() + "\'");
        System.out.println("SMTP port=" + smtpPort());
        System.out.println("SMTP protocol transport=\'" + smtpTransportProtocol() + "\'");
        System.out.println("SSL enabled=" + smtpSSLEnabled());
        System.out.println("TLS enabled=" + smtpTLSEnabled());
        System.out.println("SMTP authorization required=" + smtpAuthorizationRequired());
        System.out.println("SMTP username=\'" + smtpUsername() + "\'");
        System.out.println("SMTP password=\'" + smtpPassword() + "\'");
        System.out.println("raw properties=\'" + rawProperties() + "\'");
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


    private Properties setup(final Properties properties) {

        //read from properties
        setDebugMode(DEBUG_MODE, properties);
        setServiceHandler(SERVICE_HANDLER, properties);
        setServicePort(SERVICE_PORT, properties);
        setRecipientEmail(RECIPIENT_ADDRESS, properties);
        setSMTPHost(SMTP_HOST, properties);
        setSMTPUsername(SMTP_USERNAME, properties);
        setSMTPPassword(SMTP_PASSWORD, properties);
        setSMTPSSLEnabled(SMTP_SSL, properties);
        setSMTPTLSEnabled(SMTP_TLS, properties);

        //compute on the defined above
        computeSMTPPort();
        computeSMTPTransportProtocol();
        computeSMTPAuthorizationRequired();

        return properties;
    }


    private void setDebugMode(final String property, final Properties properties) {
        try {
            debugMode = Boolean.valueOf(properties.getProperty(property));
        } catch (Exception ex) {
            debugMode = DEFAULT_DEBUG_MODE;
        }
    }


    private void setServiceHandler(final String property, final Properties properties) {
        serviceHandler = Validate.required(property, properties.getProperty(property));

    }


    private void setServicePort(final String property, final Properties properties) {
        servicePort = Integer.parseInt(Validate.required(property, properties.getProperty(property)));
        //TODO add to xyz.enhorse.commons.Validate class methods: isBetween() and isBetweenOrEqual()
        Validate.isGreaterOrEqual(property, servicePort, PRIVATE_PORTS_MINIMAL);
        Validate.isLessOrEqual(property, servicePort, PRIVATE_PORTS_MAXIMAL);
    }


    private void setRecipientEmail(final String property, final Properties properties) {
        String address = properties.getProperty(property);

        try {
            recipientAddress = new InternetAddress(Validate.required(property, address));
        } catch (AddressException ex) {
            throw new IllegalArgumentException("\'" + address + "\' isn't a correct email address.");
        }
    }


    private void setSMTPHost(final String property, final Properties properties) {
        smtpHost = Validate.required(property, properties.getProperty(property));
    }


    private void setSMTPUsername(final String property, final Properties properties) {
        smtpUsername = properties.getProperty(property, DEFAULT_SMTP_USERNAME);
    }


    private void setSMTPPassword(final String property, final Properties properties) {
        smtpPassword = properties.getProperty(property, DEFAULT_SMTP_PASSWORD);
    }


    private void setSMTPSSLEnabled(final String property, final Properties properties) {
        try {
            smtpSSLEnabled = Boolean.valueOf(properties.getProperty(property));
        } catch (Exception ex) {
            smtpSSLEnabled = DEFAULT_SMTP_SSL;
        }
    }


    private void setSMTPTLSEnabled(final String property, final Properties properties) {
        try {
            smtpTLSEnabled = Boolean.valueOf(properties.getProperty(property));
        } catch (Exception ex) {
            smtpTLSEnabled = DEFAULT_SMTP_TLS;
        }
    }


    private void computeSMTPPort() {
        int port = BASIC_SMTP_PORT;

        if (smtpSSLEnabled) {
            port = SECURE_SMTP_PORT;
        }

        if (smtpTLSEnabled) {
            port = TLS_SMTP_PORT;
        }

        smtpPort = port;
    }


    private void computeSMTPTransportProtocol() {
        smtpTransportProtocol = (smtpSSLEnabled || smtpTLSEnabled)
                ? SMTP_SECURE_PROTOCOL
                : SMTP_BASIC_PROTOCOL;
    }


    private void computeSMTPAuthorizationRequired() {
        smtpAuthRequired = !(smtpUsername.isEmpty() && smtpPassword.isEmpty());
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
