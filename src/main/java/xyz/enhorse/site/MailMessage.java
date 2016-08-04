package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;

import java.nio.charset.Charset;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         27.07.2016
 */
public class MailMessage {

    private final String name;
    private final String address;
    private final String subject;
    private final String content;
    private final String encoding;


    private MailMessage(final String name,
                        final String address,
                        final String subject,
                        final String content,
                        final String encoding) {
        this.name = Validate.defaultIfNull(name, "");
        this.address = Validate.required("address", address);
        this.subject = Validate.defaultIfNull(subject, "");
        this.content = Validate.defaultIfNull(content, "");
        this.encoding = Validate.defaultIfNullOrEmpty(encoding, Charset.defaultCharset().name());
    }


    public String name() {
        return name;
    }


    public String address() {
        return address;
    }


    public String subject() {
        return subject;
    }


    public String content() {
        return content;
    }


    public String encoding() {
        return encoding;
    }


    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + subject.hashCode();
        result = 31 * result + content.hashCode();

        return result;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MailMessage that = (MailMessage) o;

        return name.equals(that.name)
                && address.equals(that.address)
                && subject.equals(that.subject)
                && content.equals(that.content)
                && encoding.equals(that.encoding);
    }


    @Override
    public String toString() {
        return String.format("Encoding: %s%nFrom: \'%s\' <%s>%nSubject: \'%s\'%n%s",
                encoding(), name(), address(), subject(), content());
    }


    public static class Builder {

        private String name;
        private String address;
        private String subject;
        private StringBuilder content = new StringBuilder();
        private String encoding;


        public Builder() {
        }


        public Builder setName(final String name) {
            this.name = name;
            return this;
        }


        public Builder setAddress(final String address) {
            this.address = address;
            return this;
        }


        public Builder setSubject(final String subject) {
            this.subject = subject;
            return this;
        }


        public Builder setEncoding(final String encoding) {
            this.encoding = encoding;
            return this;
        }


        public Builder addContent(final String string) {
            content.append(string);
            return this;
        }


        public Builder setContent(final String string) {
            content.setLength(0);
            content.append(string);
            return this;
        }


        public MailMessage build() {
            return new MailMessage(name, address, subject, content.toString(), encoding);
        }
    }
}
