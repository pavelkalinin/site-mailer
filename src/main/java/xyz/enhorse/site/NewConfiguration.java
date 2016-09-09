package xyz.enhorse.site;

import org.apache.log4j.Logger;
import xyz.enhorse.commons.parameters.Parameter;
import xyz.enhorse.commons.parameters.ParameterValue;
import xyz.enhorse.commons.parameters.Parameters;
import xyz.enhorse.commons.parameters.loaders.Loader;
import xyz.enhorse.commons.parameters.loaders.TextFileLoader;
import xyz.enhorse.commons.parameters.schemas.Schema;
import xyz.enhorse.site.mail.SMTPServer;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static xyz.enhorse.site.ServiceProperties.SMTP_SERVER;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.08.2016
 */
public class NewConfiguration {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class);

    private final Parameters parameters;


    private NewConfiguration(final Parameters properties) {
        parameters = properties;
        LOGGER.info(String.format("Configuration %s has been successfully loaded", toString()));
    }


    @Override
    public String toString() {
        return parameters.toString();
    }


    public static NewConfiguration loadFromFile(final String filename) {

        try {
            Loader loader = new TextFileLoader(filename, UTF_8);
            Schema schema = ServiceProperties.schema();
            Map<String, String> properties = loader.load(ConfigurationLoaderCompanion.INSTANCE);
            Parameters parameters = schema.process(properties);
            parameters = defineAbsent(parameters);

            SMTPServer server = new SMTPServer(properties);
            parameters.replace(new Parameter<>(SMTP_SERVER.property(), server));

            return new NewConfiguration(parameters);

        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Error loading the configuration file \'" + filename + "\'.");
        }
    }


    private static Parameters defineAbsent(final Parameters parameters) {
        for (final String parameter : parameters) {
            ParameterValue<?> value = parameters.get(parameter);

            if (value.value() == null) {
                ServiceProperties property = ServiceProperties.findByProperty(parameter);
                ParameterValue<?> parentValue = parameters.get(property.parent().property());
                parameters.replace(new Parameter<>(parameter, parentValue.value()));
            }
        }
        return parameters;
    }


    public static void main(String[] args) {
        NewConfiguration.loadFromFile("D:\\Other\\Projects\\site-mailer\\src\\test\\resources\\test.access.properties");
    }
}
