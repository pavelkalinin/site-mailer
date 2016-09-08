package xyz.enhorse.site;

import xyz.enhorse.commons.parameters.schemas.Description;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         12.08.2016
 */
public interface ConfigurationProperties {

    String property();

    Description<?> description();
}
