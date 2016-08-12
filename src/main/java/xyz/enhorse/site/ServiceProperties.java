package xyz.enhorse.site;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         12.08.2016
 */
public enum ServiceProperties implements ConfigurationProperties {
    HANDLER("service.handler"),
    PORT("service.port"),
    EMAIL_TO("email.to"),
    EMAIL_FROM("email.from"),
    EMAIL_ADMIN("email.admin"),
    DEBUG_SERVICE("service.debug"),
    DEBUG_JETTY("jetty.debug");

    private final String value;


    ServiceProperties(final String property) {
        value = property;
    }


    public String property() {
        return value;
    }


    @Override
    public String toString() {
        return property();
    }
}