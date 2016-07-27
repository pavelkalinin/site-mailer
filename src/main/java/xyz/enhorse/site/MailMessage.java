package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         27.07.2016
 */
public class MailMessage {

    private final Email recipient;
    private final Email sender;
    private final String subject;
    private final String message;


    private MailMessage(final Email recipient, final String sender, final String subject, final String message) {
        this.recipient = Validate.notNull("mail message recipient", recipient);
        this.sender = Email.parse(sender);
        this.subject = Validate.defaultIfNull(subject, "It sent from site");
        this.message = Validate.defaultIfNull(message, "The message is empty");
    }


    public Email recipient() {
        return recipient;
    }


    public Email sender() {
        return sender;
    }


    public String subject() {
        return subject;
    }


    public String message() {
        return message;
    }


    @Override
    public int hashCode() {
        int result = recipient.hashCode();
        result = 31 * result + sender.hashCode();
        result = 31 * result + subject.hashCode();
        result = 31 * result + message.hashCode();

        return result;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MailMessage that = (MailMessage) o;

        return recipient.equals(that.recipient)
                && sender.equals(that.sender)
                && subject.equals(that.subject)
                && message.equals(that.message);
    }


    @Override
    public String toString() {
        return "from:\'" + sender()
                + "\' to:\'" + recipient()
                + "\'\n\"" + subject()
                + "\"\n" + message();
    }


    public static class Builder {

        private Email recipient;
        private String sender;
        private String subject;
        private String message;


        public Builder(final Email recipient) {
            this.recipient = recipient;
        }


        Builder addSender(final String sender) {
            this.sender = sender;
            return this;
        }


        Builder addSubject(final String subject) {
            this.subject = subject;
            return this;
        }


        Builder addMessage(final String message) {
            this.message = message;
            return this;
        }


        MailMessage build() {
            return new MailMessage(recipient, sender, subject, message);
        }
    }
}