package xyz.enhorse.site;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         21.01.2016
 */
public class EmailTest {

    private final static String VALID_EMAIL_ADDRESS = "postbox@domain.zone";
    private final static Email VALID_EMAIL = Email.parse(VALID_EMAIL_ADDRESS);


    @Test
    public void parse() throws Exception {
        assertNotNull(Email.parse(VALID_EMAIL_ADDRESS));
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_withoutDot() throws Exception {
        assertNotNull(Email.parse("postbox@zone"));
    }


    @Test
    public void parse_manyDots() throws Exception {
        assertNotNull(Email.parse("postbox@sub.domain.zone"));
    }


    @Test
    public void parse_tailSpace() throws Exception {
        assertNotNull(Email.parse("postbox@domain.zone "));
    }


    @Test
    public void parse_startsWithSpace() throws Exception {
        assertNotNull(Email.parse(" postbox@domain.zone"));
    }


    @Test
    public void getAddress() throws Exception {
        assertEquals("postbox@domain.zone", VALID_EMAIL.getAddress());
    }


    @Test
    public void getPostbox_valid() throws Exception {
        assertEquals("postbox", VALID_EMAIL.getPostbox());
    }


    @Test
    public void getPostbox_manyDots() throws Exception {
        assertEquals("post.box", Email.parse("post.box@sub.domain.zone").getPostbox());
    }


    @Test
    public void getDomain() throws Exception {
        assertEquals("domain.zone", VALID_EMAIL.getDomain());
    }


    @Test
    public void getDomain_manyDots() throws Exception {
        assertEquals("sub.domain.zone", Email.parse("postbox@sub.domain.zone").getDomain());
    }


    @Test
    public void getZone() throws Exception {
        assertEquals("zone", VALID_EMAIL.getZone());
    }


    @Test
    public void getZone_manyDots() throws Exception {
        assertEquals("zone", Email.parse("postbox@sub.domain.zone").getZone());
    }


    @Test
    public void toString_valid() throws Exception {
        assertNotNull(Email.parse(VALID_EMAIL_ADDRESS).toString());
    }


    @Test
    public void hashCode_same() throws Exception {
        int first = Email.parse("postbox@domain.zone").hashCode();
        int second = Email.parse("postbox@domain.zone").hashCode();
        assertEquals(first, second);
    }


    @Test
    public void hashCode_differentPostboxes() throws Exception {
        int first = Email.parse("postbox1@domain.zone").hashCode();
        int second = Email.parse("postbox2@domain.zone").hashCode();
        assertNotEquals(first, second);
    }


    @Test
    public void hashCode_differentDomains() throws Exception {
        int first = Email.parse("postbox@domain1.zone").hashCode();
        int second = Email.parse("postbox@domain2.zone").hashCode();
        assertNotEquals(first, second);
    }


    @Test
    public void hashCode_differentZones() throws Exception {
        int first = Email.parse("postbox@domain.net").hashCode();
        int second = Email.parse("postbox@domain.com").hashCode();
        assertNotEquals(first, second);
    }


    @Test
    public void hashCode_differentAddresses() throws Exception {
        int first = Email.parse("postbox@sub.domain.net").hashCode();
        int second = Email.parse("postbox@domain.com").hashCode();
        assertNotEquals(first, second);
    }


    @Test
    public void equals_same() throws Exception {
        Email email = Email.parse("postbox@domain.zone");
        assertEquals(email, email);
    }


    @Test
    public void equals_null() throws Exception {
        Email email = Email.parse("postbox@domain.zone");
        assertNotEquals(email, null);
    }


    @Test
    public void equals_identical() throws Exception {
        Email first = Email.parse("postbox@domain.zone");
        Email second = Email.parse("postbox@domain.zone");
        assertEquals(first, second);
    }


    @Test
    public void equals_differentPostboxes() throws Exception {
        Email first = Email.parse("postbox1@domain.zone");
        Email second = Email.parse("postbox2@domain.zone");
        assertNotEquals(first, second);
    }


    @Test
    public void equals_differentDomains() throws Exception {
        Email first = Email.parse("postbox@domain1.zone");
        Email second = Email.parse("postbox@domain2.zone");
        assertNotEquals(first, second);
    }


    @Test
    public void equals_differentZones() throws Exception {
        Email first = Email.parse("postbox@domain.com");
        Email second = Email.parse("postbox@domain.net");
        assertNotEquals(first, second);
    }


    @Test
    public void equals_differentAddresses() throws Exception {
        Email first = Email.parse("post@domain.com");
        Email second = Email.parse("box@tomain.net");
        assertNotEquals(first, second);
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_withoutAt() throws Exception {
        Email.parse("postbox.zone");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_manyAts() throws Exception {
        Email.parse("postbox@zone@zone");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_tailAt() throws Exception {
        Email.parse("postbox@");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_startsWithAt() throws Exception {
        Email.parse("@postbox");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_empty() throws Exception {
        Email.parse("");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_onlySpace() throws Exception {
        Email.parse(" ");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_onlySpaces() throws Exception {
        Email.parse("  ");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_spaceIntoPostbox() throws Exception {
        Email.parse("post box@domain.zone");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_spaceIntoDomain() throws Exception {
        Email.parse("postbox@sub domain.zone");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_spaceIntoZone() throws Exception {
        Email.parse("postbox@subdomain.zo ne");
    }


    @Test(expected = IllegalArgumentException.class)
    public void parse_null() throws Exception {
        Email.parse(null);
    }
}