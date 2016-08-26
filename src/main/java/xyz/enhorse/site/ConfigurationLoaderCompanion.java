package xyz.enhorse.site;

import xyz.enhorse.commons.Check;
import xyz.enhorse.commons.parameters.LoaderCompanion;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.08.2016
 */
public class ConfigurationLoaderCompanion implements LoaderCompanion {

    private ConfigurationLoaderCompanion() {

    }
    

    @Override
    public String preProcessKey(final String key) {
        if (!Check.isNullOrEmpty(key)) {
            return key.trim();
        }

        return key;
    }


    @Override
    public String postProcessValue(final String value) {
        if (!Check.isNullOrEmpty(value)) {
            return trimQuotes(value.trim());
        }

        return value;
    }


    private String trimQuotes(final String string) {
        String result = string;
        if (string.charAt(0) == '\"') {
            result = string.substring(1);
            int tail = result.length() - 1;
            if (result.charAt(tail) == '\"') {
                result = result.substring(0, tail);
            }
        }

        return result;
    }


    public static ConfigurationLoaderCompanion instance() {
        return SingletonHolder.instance();
    }


    private static class SingletonHolder {


        public static ConfigurationLoaderCompanion instance = null;


        public static ConfigurationLoaderCompanion instance() {
            if (instance == null) {
                instance = new ConfigurationLoaderCompanion();
            }
            return instance;
        }


    }
}
