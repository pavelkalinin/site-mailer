package xyz.enhorse.site;

import org.apache.log4j.Logger;
import xyz.enhorse.commons.parameters.ConcurrentParameters;
import xyz.enhorse.commons.parameters.Parameters;
import xyz.enhorse.commons.parameters.ParametersLoader;
import xyz.enhorse.commons.parameters.TextFileLoader;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.08.2016
 */
public class NewConfiguration {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class);

    private static final int PRIVATE_PORTS_MINIMAL = 49152;
    private static final int PRIVATE_PORTS_MAXIMAL = 65535;
    private static final String CONTEXT_PATH_PREFIX = "/";

    private final Parameters parameters;


    private NewConfiguration(final Parameters properties) {
        parameters = properties;
        LOGGER.info(String.format("Configuration %s has been successfully loaded", toString()));
    }


    public static NewConfiguration loadFromFile(final String filename) {
        Parameters parameters = new ConcurrentParameters();
        try {
            ParametersLoader loader = new TextFileLoader(filename, StandardCharsets.UTF_8);
            for (Map.Entry<String, String> parameter : loader.load(ConfigurationLoaderCompanion.instance()).entrySet()) {
                parameters.put(parameter.getKey(), parameter.getValue());
            }
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Error loading the configuration file \'" + filename + "\'.");
        }

        return new NewConfiguration(parameters);
    }
}
