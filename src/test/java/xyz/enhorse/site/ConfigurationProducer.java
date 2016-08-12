package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;
import xyz.enhorse.site.mail.SMTPProperties;
import xyz.enhorse.site.mail.SMTPProtocols;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         12.08.2016
 */
public class ConfigurationProducer {

    private final static Properties EMPTY = new Properties();
    public final static ConfigurationProducer EMPTY_CONFIG = new ConfigurationProducer(EMPTY);
    private final static Properties SERVICE = fillServiceProperties();
    public final static ConfigurationProducer SERVICE_CONFIG = new ConfigurationProducer(SERVICE);
    private final static Properties SMTP = fillSMTPProperties();
    public final static ConfigurationProducer SMTP_CONFIG = new ConfigurationProducer(SMTP);


    private final Properties current;


    private ConfigurationProducer(final Properties properties) {
        current = properties;
    }


    private ConfigurationProducer(final ConfigurationProducer producer) {
        current = producer.properties();
    }


    private Properties properties() {
        final Properties properties = new Properties();

        for (Map.Entry<Object, Object> entry : current.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }

        return properties;
    }


    public ConfigurationProducer putProperty(final ConfigurationProperties property, final String value) {
        final String name = Validate.required("property", property.property());

        if (current.getProperty(name) != null) {
            current.remove(name);
        }

        current.put(name, value);
        return new ConfigurationProducer(this);
    }


    public ConfigurationProducer removeProperty(final ConfigurationProperties property) {
        final String name = Validate.required("property", property.property());

        if (current.getProperty(name) != null) {
            current.remove(name);
            return new ConfigurationProducer(this);
        }

        return this;
    }


    public File saveTo(final File file) {
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"))) {
                current.store(writer, "");
            } catch (IOException ex) {
                throw new IllegalStateException("Error saving a configuration to file \'" + file + "\'", ex);
            }
        }

        return file;
    }


    private static Properties fillSMTPProperties() {
        final Properties properties = new Properties();

        properties.put(SMTPProperties.AUTH, false);
        properties.put(SMTPProperties.DEBUG, false);
        properties.put(SMTPProperties.HOST, "smtp.server.com");
        properties.put(SMTPProperties.MAILER, "mailer");
        properties.put(SMTPProperties.PASSWORD, "password");
        properties.put(SMTPProperties.PORT, 25);
        properties.put(SMTPProperties.PROTOCOL, SMTPProtocols.BASIC);
        properties.put(SMTPProperties.SSL, false);
        properties.put(SMTPProperties.TLS, false);
        properties.put(SMTPProperties.USER, "user");

        return properties;
    }


    private static Properties fillServiceProperties() {
        final Properties properties = new Properties();

        properties.put(ServiceProperties.DEBUG_JETTY, false);
        properties.put(ServiceProperties.DEBUG_SERVICE, false);
        properties.put(ServiceProperties.EMAIL_ADMIN, "admin@mail.com");
        properties.put(ServiceProperties.EMAIL_FROM, "admin@mail.com");
        properties.put(ServiceProperties.EMAIL_TO, "admin@mail.com");
        properties.put(ServiceProperties.HANDLER, "/handler");
        properties.put(ServiceProperties.PORT, 50000);

        return properties;
    }
}
