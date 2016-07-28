package xyz.enhorse.site.mail;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public enum SMTPProtocols {
    BASIC("smtp"),
    SECURE("smtps");

    private final String tag;


    SMTPProtocols(final String string) {
        tag = string;
    }


    public String tag() {
        return tag;
    }
}
