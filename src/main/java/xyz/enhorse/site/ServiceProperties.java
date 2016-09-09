package xyz.enhorse.site;

import xyz.enhorse.commons.parameters.schemas.Description;
import xyz.enhorse.commons.parameters.schemas.Element;
import xyz.enhorse.commons.parameters.schemas.HashSchema;
import xyz.enhorse.commons.parameters.schemas.Schema;
import xyz.enhorse.commons.parameters.schemas.constraints.NotNullConstraint;
import xyz.enhorse.commons.parameters.schemas.constraints.NumberInRangeInclusive;
import xyz.enhorse.commons.parameters.schemas.constraints.StringConstraints;

import static xyz.enhorse.commons.PureTypes.*;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         12.08.2016
 */
public enum ServiceProperties {
    HANDLER("service.handler") {
        @Override
        public Description<?> description() {
            return new Description<>(STRING,
                    null,
                    new NotNullConstraint<>(), StringConstraints.NOT_EMPTY);
        }


        @Override
        public ServiceProperties parent() {
            return this;
        }
    },

    PORT("service.port") {
        @Override
        public Description<?> description() {
            return new Description<>(INTEGER,
                    null,
                    new NotNullConstraint<>(), new NumberInRangeInclusive<>(PRIVATE_PORTS_MINIMAL, PRIVATE_PORTS_MAXIMAL));
        }


        @Override
        public ServiceProperties parent() {
            return this;
        }
    },

    EMAIL_TO("email.to") {
        @Override
        public Description<?> description() {
            return new Description<>(STRING,
                    null,
                    new NotNullConstraint<>(), StringConstraints.NOT_EMPTY, StringConstraints.E_MAIL);
        }


        @Override
        public ServiceProperties parent() {
            return this;
        }
    },

    EMAIL_FROM("email.from") {
        @Override
        public Description<?> description() {
            return new Description<>(STRING,
                    null,
                    StringConstraints.NOT_EMPTY, StringConstraints.E_MAIL);
        }


        @Override
        public ServiceProperties parent() {
            return EMAIL_TO;
        }
    },

    EMAIL_ADMIN("email.admin") {
        @Override
        public Description<?> description() {
            return new Description<>(STRING,
                    null,
                    StringConstraints.NOT_EMPTY, StringConstraints.E_MAIL);
        }


        @Override
        public ServiceProperties parent() {
            return EMAIL_TO;
        }
    },

    DEBUG_SERVICE("service.debug") {
        @Override
        public Description<?> description() {
            return new Description<>(BOOLEAN,
                    () -> false);
        }


        @Override
        public ServiceProperties parent() {
            return this;
        }
    },

    DEBUG_JETTY("jetty.debug") {
        @Override
        public Description<?> description() {
            return new Description<>(BOOLEAN,
                    () -> false);
        }


        @Override
        public ServiceProperties parent() {
            return this;
        }
    },

    SMTP_SERVER("smtp.server") {
        @Override
        public Description<?> description() {
            return new Description<>(NULL,
                    () -> null);
        }


        @Override
        public ServiceProperties parent() {
            return this;
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


    abstract Description<?> description();

    abstract ServiceProperties parent();


    public static Schema schema() {
        Schema schema = new HashSchema();
        for (ServiceProperties property : values()) {
            schema.put(new Element<>(property.property(), property.description()));
        }

        return schema;
    }


    public static ServiceProperties findByProperty(final String property) {
        if (property != null) {
            for (ServiceProperties p : values()) {
                if (p.property().equalsIgnoreCase(property)) {
                    return p;
                }
            }
        }

        throw new IllegalArgumentException("Unknown property \'" + property + "\'");
    }
}