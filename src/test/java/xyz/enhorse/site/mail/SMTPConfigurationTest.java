package xyz.enhorse.site.mail;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import xyz.enhorse.site.Configuration;

import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static xyz.enhorse.site.PropertiesFileProducer.smtpProperties;
import static xyz.enhorse.site.mail.SMTPProperties.HOST;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         16.08.2016
 */
public class SMTPConfigurationTest {

    private static Level LEVEL;
    private static TemporaryFolder temp;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void create_full() throws Exception {
        Properties properties = smtpProperties().properties();

        assertNotNull(new SMTPConfiguration(properties));
    }


    @Test
    public void create_illegalProperties_null() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("null");

        assertNotNull(new SMTPConfiguration(null));
    }


    @Test
    public void propertySmtpServer_absent() throws Exception {
        Properties properties = smtpProperties()
                .removeProperty(HOST)
                .properties();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(HOST.property());

        assertNotNull(new SMTPConfiguration(properties));
    }


    @Test
    public void propertySmtpServer_empty() throws Exception {
        Properties properties = smtpProperties()
                .addProperty(HOST, "")
                .properties();


        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(HOST.property());

        assertNotNull(new SMTPConfiguration(properties));
    }


    @Test
    public void propertySmtpServer_incorrect() throws Exception {
        Properties properties = smtpProperties()
                .addProperty(HOST, "invalid host")
                .properties();


        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(HOST.property());

        assertNotNull(new SMTPConfiguration(properties));
    }


    @BeforeClass
    public static void setUp() throws Exception {
        temp = new TemporaryFolder();
        temp.create();
        LEVEL = Logger.getLogger(Configuration.class).getLevel();
        Logger.getLogger(Configuration.class).setLevel(Level.FATAL);
    }


    @AfterClass
    public static void tearDown() throws Exception {
        temp.delete();
        Logger.getLogger(Configuration.class).setLevel(LEVEL);
    }
}