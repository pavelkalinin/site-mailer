package xyz.enhorse.site;

import xyz.enhorse.commons.Check;
import xyz.enhorse.commons.parameters.loaders.Companion;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.08.2016
 */
public enum ConfigurationLoaderCompanion implements Companion {
    INSTANCE;

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
}