package xyz.enhorse.site.mail;

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
    },
    PORT("smtp.port") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".port";
        }
    },
    SENDER("smtp.sender") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".sender";
        }
    },
    USER("smtp.user") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".user";
        }
    },
    PASSWORD("smtp.password") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".password";
        }
    },
    AUTH("smtp.auth") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".auth";
        }
    },
    SSL("smtp.ssl") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".ssl.enable";
        }
    },
    TLS("smtp.tls") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".starttls.enable";
        }
    },
    MAILER("smtp.x-mailer") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail." + protocol.tag() + ".x-mailer";
        }
    },
    DEBUG("smtp.debug") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail.debug";
        }
    },
    PROTOCOL("smtp.protocol") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail.transport.protocol";
        }
    },
    CHARSET("smtp.charset") {
        @Override
        public String of(final SMTPProtocols protocol) {
            return "mail.charset";
        }
    };


    private final String property;


    SMTPProperties(final String property) {
        this.property = property;
    }


    public String property() {
        return property;
    }


    public abstract String of(final SMTPProtocols protocol);
}
