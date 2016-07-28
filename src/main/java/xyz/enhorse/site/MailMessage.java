package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         27.07.2016
 */
public class MailMessage {

    private final String from;
    private final String email;
    private final String subject;
    private final String content;


    private MailMessage(final String from, final String email, final String subject, final String content) {
        this.from = Validate.required("from", from);
        this.email = Validate.required("email", email);
        this.subject = Validate.required("subject", subject);
        this.content = Validate.required("content", content);
    }


    public String from() {
        return from;
    }


    public String email() {
        return email;
    }


    public String subject() {
        return subject;
    }


    public String content() {
        return content;
    }


    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + subject.hashCode();
        result = 31 * result + content.hashCode();

        return result;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MailMessage that = (MailMessage) o;

        return from.equals(that.from)
                && email.equals(that.email)
                && subject.equals(that.subject)
                && content.equals(that.content);
    }


    @Override
    public String toString() {
        return String.format("\'%s\'<%s>%n\'%s\'%n\"%s\"", from(), email(), subject(), content());
    }


    public static class Builder {

        private String name;
        private String email;
        private String subject;
        private String message;


        public Builder() {
        }


        public Builder addFrom(final String name) {
            this.name = name;
            return this;
        }


        public Builder addEmail(final String email) {
            this.email = email;
            return this;
        }


        public Builder addSubject(final String subject) {
            this.subject = subject;
            return this;
        }


        public Builder addMessage(final String message) {
            this.message = message;
            return this;
        }


        public MailMessage build() {
            return new MailMessage(name, email, subject, message);
        }
    }
}
