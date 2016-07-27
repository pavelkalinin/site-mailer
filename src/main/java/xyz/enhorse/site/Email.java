package xyz.enhorse.site;

import org.apache.commons.validator.routines.EmailValidator;
import xyz.enhorse.commons.Validate;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         21.01.2016
 */
public final class Email {

    private String address;


    private Email() {
    }


    public String getAddress() {
        return Validate.defaultIfNull(address, "");
    }


    private void setAddress(String value) {
        address = value.trim();
    }


    public String getPostbox() {
        int posOfAt = getAddress().indexOf('@');
        return getAddress().substring(0, posOfAt);
    }


    public String getDomain() {
        int posOfAt = getAddress().indexOf('@');
        return getAddress().substring(posOfAt + 1);
    }


    public String getZone() {
        int lastDotPos = getAddress().lastIndexOf('.');
        return (lastDotPos > 0)
                ? getAddress().substring(lastDotPos + 1)
                : "";
    }


    @Override
    public int hashCode() {
        return getAddress().hashCode();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Email email = (Email) o;

        return getAddress().equals(email.getAddress());
    }


    @Override
    public String toString() {
        return getAddress();
    }


    public static Email parse(final String address) {
        if ((address != null) && (EmailValidator.getInstance().isValid(address))) {
            Email result = new Email();
            result.setAddress(address);
            return result;
        }
        throw new IllegalArgumentException("\'" + address + "\' isn't a correct email address.");
    }
}
