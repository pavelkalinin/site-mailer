package xyz.enhorse.site.mail;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public enum SMTPConfigurationProperties {
    HOST("smtp.host") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".host";
        }
    },
    PORT("smtp.port") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".port";
        }
    },
    SENDER("smtp.sender") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".sender";
        }
    },
    USER("smtp.user") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".user";
        }
    },
    PASSWORD("smtp.password") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".password";
        }
    },
    SSL("smtp.ssl") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".ssl.enable";
        }
    },
    TLS("smtp.tls") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".starttls.enable";
        }
    },
    AUTH("smtp.auth") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".auth";
        }
    },
    DEBUG("smtp.debug") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail.debug";
        }
    },
    PROTOCOL("smtp.protocol") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail.transport.protocol";
        }
    },
    CHARSET("smtp.charset") {
        @Override
        public String forProtocol(final SMTPProtocols protocol) {
            return "mail.charset";
        }
    };


    private final String property;


    SMTPConfigurationProperties(final String property) {
        this.property = property;
    }


    public String property() {
        return property;
    }


    public abstract String forProtocol(final SMTPProtocols protocol);
}
