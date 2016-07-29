package xyz.enhorse.site.mail;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public enum SMTPProtocols {
    BASIC("smtp", 25),
    SECURE("smtps", 465);

    private final String tag;
    private final int port;


    SMTPProtocols(final String string, final int defaultPort) {
        tag = string;
        port = defaultPort;
    }


    public String tag() {
        return tag;
    }


    public int port() {
        return port;
    }
}
