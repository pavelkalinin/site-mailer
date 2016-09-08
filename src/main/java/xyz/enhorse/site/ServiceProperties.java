package xyz.enhorse.site;

import xyz.enhorse.commons.PureTypes;
import xyz.enhorse.commons.parameters.schemas.Description;
import xyz.enhorse.commons.parameters.schemas.Element;
import xyz.enhorse.commons.parameters.schemas.HashSchema;
import xyz.enhorse.commons.parameters.schemas.Schema;
import xyz.enhorse.commons.parameters.schemas.constraints.NotNullConstraint;
import xyz.enhorse.commons.parameters.schemas.constraints.NumberInRangeInclusive;
import xyz.enhorse.commons.parameters.schemas.constraints.StringConstraints;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         12.08.2016
 */
public enum ServiceProperties implements ConfigurationProperties {
    HANDLER("service.handler") {
        @Override
        public Description<?> description() {
            return new Description<>(PureTypes.STRING,
                    null,
                    new NotNullConstraint<>(), StringConstraints.NOT_EMPTY);
        }
    },

    PORT("service.port") {
        @Override
        public Description<?> description() {
            return new Description<>(PureTypes.INTEGER,
                    null,
                    new NotNullConstraint<>(), new NumberInRangeInclusive<>(PRIVATE_PORTS_MINIMAL, PRIVATE_PORTS_MAXIMAL));
        }
    },
    
    EMAIL_TO("email.to") {
        @Override
        public Description<?> description() {
            return new Description<>(PureTypes.STRING,
                    null,
                    new NotNullConstraint<>(), StringConstraints.NOT_EMPTY, StringConstraints.E_MAIL);
        }
    },

    EMAIL_FROM("email.from") {
        @Override
        public Description<?> description() {
            return new Description<>(PureTypes.STRING,
                    null,
                    StringConstraints.NOT_EMPTY, StringConstraints.E_MAIL);
        }
    },

    EMAIL_ADMIN("email.admin") {
        @Override
        public Description<?> description() {
            return new Description<>(PureTypes.STRING,
                    null,
                    new NotNullConstraint<>(), StringConstraints.NOT_EMPTY, StringConstraints.E_MAIL);
        }
    },

    DEBUG_SERVICE("service.debug") {
        @Override
        public Description<?> description() {
            return new Description<>(PureTypes.BOOLEAN,
                    () -> false,
                    null);
        }
    },

    DEBUG_JETTY("jetty.debug") {
        @Override
        public Description<?> description() {
            return new Description<>(PureTypes.BOOLEAN,
                    () -> false,
                    null);
        }
    },

    SMTP_SERVER("smtp.server") {
        @Override
        public Description<?> description() {
            return new Description<>(PureTypes.NULL,
                    () -> null,
                    null);
        }
    };


    private static final int PRIVATE_PORTS_MINIMAL = 49152;
    private static final int PRIVATE_PORTS_MAXIMAL = 65535;

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


    public static Schema schema() {
        Schema schema = new HashSchema();
        for (ServiceProperties property : values()) {
            schema.put(new Element<>(property.property(), property.description()));
        }

        return schema;
    }
}