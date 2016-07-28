package xyz.enhorse.site;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public enum SMTPConfigurationProperties {
    HOST("smtp.host") {
        @Override
        public String translate(final SMTPProtocols protocol) {
            return "mail." + protocol.definition() + ".host";
        }
    },
    PORT("smtp.port") {
        @Override
        public String translate(final SMTPProtocols protocol) {
            return "mail." + protocol.definition() + ".port";
        }
    },
    USER("smtp.user") {
        @Override
        public String translate(final SMTPProtocols protocol) {
            return "mail." + protocol.definition() + ".user";
        }
    },
    PASSWORD("smtp.password") {
        @Override
        public String translate(final SMTPProtocols protocol) {
            return "mail." + protocol.definition() + ".password";
        }
    },
    SSL("smtp.ssl") {
        @Override
        public String translate(final SMTPProtocols protocol) {
            return "mail." + protocol.definition() + ".ssl.enable";
        }
    },
    TLS("smtp.tls") {
        @Override
        public String translate(final SMTPProtocols protocol) {
            return "mail." + protocol.definition() + ".starttls.enable";
        }
    },
    AUTH("smtp.auth") {
        @Override
        public String translate(final SMTPProtocols protocol) {
            return "mail." + protocol.definition() + ".auth";
        }
    };


    private final String property;


    SMTPConfigurationProperties(final String property) {
        this.property = property;
    }


    public String property() {
        return property;
    }


    public SMTPConfigurationProperties get(String property) {
        for (SMTPConfigurationProperties value : values()) {
            if (value.property.equals(property)) {
                return value;
            }
        }

        throw new IllegalArgumentException("\'" + property + "\' isn't a valid BASIC configuration property.");
    }


    public abstract String translate(final SMTPProtocols protocol);
}
