package xyz.enhorse.site;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public enum SMTPProtocols {
    BASIC("smtp"),
    SECURE("smtps");

    private final String name;


    SMTPProtocols(final String name) {
        this.name = name;
    }


    public String definition() {
        return name;
    }
}
