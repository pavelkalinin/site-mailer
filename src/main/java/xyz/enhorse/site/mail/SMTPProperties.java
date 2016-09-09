package xyz.enhorse.site.mail;

import xyz.enhorse.commons.parameters.schemas.Description;
import xyz.enhorse.commons.parameters.schemas.Element;
import xyz.enhorse.commons.parameters.schemas.HashSchema;
import xyz.enhorse.commons.parameters.schemas.Schema;
import xyz.enhorse.commons.parameters.schemas.constraints.NotNullConstraint;
import xyz.enhorse.commons.parameters.schemas.constraints.StringConstraints;

import static xyz.enhorse.commons.PureTypes.*;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public enum SMTPProperties {
    HOST("smtp.host") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".host";
        }


        @Override
        public Description<?> description() {
            return new Description<>(STRING,
                    null,
                    new NotNullConstraint<>(), StringConstraints.NOT_EMPTY);
        }
    },


    PORT("smtp.port") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".port";
        }


        @Override
        public Description<?> description() {
            return new Description<>(INTEGER,
                    null);
        }
    },


    USER("smtp.user") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".user";
        }


        @Override
        public Description<?> description() {
            return new Description<>(STRING,
                    () -> "");
        }
    },


    PASSWORD("smtp.password") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".password";
        }


        @Override
        public Description<?> description() {
            return new Description<>(STRING,
                    () -> "");
        }
    },


    AUTH("smtp.auth") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".auth";
        }


        @Override
        public Description<?> description() {
            return new Description<>(BOOLEAN,
                    () -> false);
        }
    },


    SSL("smtp.ssl") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".ssl.enable";
        }


        @Override
        public Description<?> description() {
            return new Description<>(BOOLEAN,
                    () -> false);
        }
    },


    TLS("smtp.tls") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".starttls.enable";
        }


        @Override
        public Description<?> description() {
            return new Description<>(BOOLEAN,
                    () -> false);
        }
    },


    MAILER("smtp.x-mailer") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".x-mailer";
        }


        @Override
        public Description<?> description() {
            return new Description<>(STRING,
                    () -> "");
        }
    },


    DEBUG("smtp.debug") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail.debug";
        }


        @Override
        public Description<?> description() {
            return new Description<>(BOOLEAN,
                    () -> false);
        }
    },


    PROTOCOL("smtp.protocol") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail.transport.protocol";
        }


        @Override
        public Description<?> description() {
            return new Description<>(NULL,
                    null);
        }
    };


    private final String value;


    SMTPProperties(final String property) {
        value = property;
    }


    public String property() {
        return value;
    }


    abstract public Description<?> description();


    abstract public String of(final SMTPProtocols protocol);


    @Override
    public String toString() {
        return property();
    }


    public static Schema schema() {
        Schema schema = new HashSchema();
        for (SMTPProperties property : values()) {
            schema.put(new Element<>(property.property(), property.description()));
        }

        return schema;
    }
}
