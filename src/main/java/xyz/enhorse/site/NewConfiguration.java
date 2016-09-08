package xyz.enhorse.site;

import org.apache.log4j.Logger;
import xyz.enhorse.commons.parameters.Parameters;
import xyz.enhorse.commons.parameters.loaders.Loader;
import xyz.enhorse.commons.parameters.loaders.TextFileLoader;
import xyz.enhorse.commons.parameters.schemas.Schema;

import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.08.2016
 */
public class NewConfiguration {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class);

    private static final String CONTEXT_PATH_PREFIX = "/";

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
            Schema schema = ServiceProperties.schema();
            Loader loader = new TextFileLoader(filename, StandardCharsets.UTF_8);

            return new NewConfiguration(schema.process(loader.load(ConfigurationLoaderCompanion.INSTANCE)));
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Error loading the configuration file \'" + filename + "\'.");
        }
    }


    public static void main(String[] args) {
        System.out.println(NewConfiguration.loadFromFile("/Volumes/USR/Projects/site-mailer/src/test/resources/test.access.properties"));
    }
}
