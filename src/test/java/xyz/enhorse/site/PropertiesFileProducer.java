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
public class PropertiesFileProducer {

    private final Properties current;


    private PropertiesFileProducer(final Properties properties) {
        current = appendProperties(new Properties(), properties);
    }


    private PropertiesFileProducer(final PropertiesFileProducer producer) {
        current = producer.properties();
    }


    public Properties properties() {
        return appendProperties(new Properties(), current);
    }


    public PropertiesFileProducer addProperty(final ServiceProperties property, final String value) {
        final String name = Validate.required("property", property.property());

        if (current.getProperty(name) != null) {
            current.remove(name);
        }

        current.put(name, value);
        return new PropertiesFileProducer(this);
    }


    public PropertiesFileProducer removeProperty(final ServiceProperties property) {
        final String name = Validate.required("property", property.property());

        if (current.getProperty(name) != null) {
            current.remove(name);
            return new PropertiesFileProducer(this);
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


    @Override
    public String toString() {
        return current.toString();
    }


    public static PropertiesFileProducer emptyProperties() {
        return new PropertiesFileProducer(new Properties());
    }


    public static PropertiesFileProducer allProperties() {
        return new PropertiesFileProducer(buildAllProperties());
    }


    public static PropertiesFileProducer serviceProperties() {
        return new PropertiesFileProducer(buildServiceProperties());
    }


    public static PropertiesFileProducer smtpProperties() {
        return new PropertiesFileProducer(buildSMTPProperties());
    }


    private static Properties buildSMTPProperties() {
        final Properties properties = new Properties();

        properties.put(SMTPProperties.AUTH.property(), "false");
        properties.put(SMTPProperties.DEBUG.property(), "false");
        properties.put(SMTPProperties.HOST.property(), "smtp.server.com");
        properties.put(SMTPProperties.MAILER.property(), "mailer");
        properties.put(SMTPProperties.PASSWORD.property(), "password");
        properties.put(SMTPProperties.PORT.property(), "25");
        properties.put(SMTPProperties.PROTOCOL.property(), SMTPProtocols.BASIC.tag());
        properties.put(SMTPProperties.SSL.property(), "false");
        properties.put(SMTPProperties.TLS.property(), "false");
        properties.put(SMTPProperties.USER.property(), "user");

        return properties;
    }


    private static Properties buildServiceProperties() {
        final Properties properties = new Properties();

        properties.put(ServiceProperties.DEBUG_JETTY.property(), "false");
        properties.put(ServiceProperties.DEBUG_SERVICE.property(), "false");
        properties.put(ServiceProperties.EMAIL_ADMIN.property(), "admin@mail.com");
        properties.put(ServiceProperties.EMAIL_FROM.property(), "from@mail.com");
        properties.put(ServiceProperties.EMAIL_TO.property(), "to@mail.com");
        properties.put(ServiceProperties.HANDLER.property(), "/handler");
        properties.put(ServiceProperties.PORT.property(), "50000");

        return properties;
    }


    private static Properties buildAllProperties() {
        final Properties properties = new Properties();

        appendProperties(properties, buildServiceProperties());
        appendProperties(properties, buildSMTPProperties());

        return properties;
    }


    private static Properties appendProperties(final Properties to, final Properties from) {
        for (Map.Entry<Object, Object> entry : from.entrySet()) {
            to.put(entry.getKey(), entry.getValue());
        }

        return to;
    }
}
