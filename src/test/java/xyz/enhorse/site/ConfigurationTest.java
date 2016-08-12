package xyz.enhorse.site;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;
import static xyz.enhorse.site.Configuration.loadFromFile;
import static xyz.enhorse.site.PropertiesFileProducer.allProperties;
import static xyz.enhorse.site.PropertiesFileProducer.smtpProperties;
import static xyz.enhorse.site.ServiceProperties.*;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         12.08.2016
 */
public class ConfigurationTest {

    private static TemporaryFolder temp;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void load_full() throws Exception {
        File file = temp.newFile();

        assertNotNull(loadFromFile(allProperties().saveTo(file).getAbsolutePath()));
    }


    @Test
    public void load_onlyRequired() throws Exception {
        File file = smtpProperties()
                .addProperty(HANDLER, "/handler")
                .addProperty(PORT, "50000")
                .addProperty(EMAIL_TO, "mail@mail.com")
                .saveTo(temp.newFile());

        assertNotNull(loadFromFile(file.getAbsolutePath()));
    }


    @Test
    public void load_withoutHandler() throws Exception {
        File file = allProperties()
                .removeProperty(HANDLER)
                .saveTo(temp.newFile());

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(HANDLER.property());
        assertNull(loadFromFile(file.getAbsolutePath()));
    }


    @Test
    public void load_withoutPort() throws Exception {
        File file = allProperties()
                .removeProperty(PORT)
                .saveTo(temp.newFile());

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(PORT.property());
        assertNull(loadFromFile(file.getAbsolutePath()));
    }


    @Test
    public void load_withoutEmailTo() throws Exception {
        File file = allProperties()
                .removeProperty(EMAIL_TO)
                .saveTo(temp.newFile());

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(EMAIL_TO.property());
        assertNull(loadFromFile(file.getAbsolutePath()));
    }


    @Test
    public void toString_outputAllProperties() throws Exception {
        File file = temp.newFile();

        String string = loadFromFile(allProperties().saveTo(file).getAbsolutePath()).toString();

        for (ConfigurationProperties property : ServiceProperties.values()) {
            assertEquals(String.format("toString%n%s%n" + "should contains \'%s\'", string, property.property()),
                    true, string.contains(property.property()));
        }
    }


    @BeforeClass
    public static void setUp() throws Exception {
        temp = new TemporaryFolder();
        temp.create();
    }


    @AfterClass
    public static void tearDown() throws Exception {
        temp.delete();
    }

}