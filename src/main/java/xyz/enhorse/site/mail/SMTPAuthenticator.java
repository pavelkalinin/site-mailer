package xyz.enhorse.site.mail;

import xyz.enhorse.commons.Validate;

import javax.mail.PasswordAuthentication;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         28.07.2016
 */
public class SMTPAuthenticator extends javax.mail.Authenticator {

    private final String user;
    private final String password;


    public SMTPAuthenticator(String user, String password) {
        this.user = Validate.notNull("user for authenticator", user);
        this.password = Validate.notNull("password for authenticator", password);
    }


    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }
}
