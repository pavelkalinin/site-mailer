package xyz.enhorse.site;

import org.apache.log4j.Logger;
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
    public void serviceHandler_equalsServiceHandler() throws Exception {
        String expected = "/super-handler";
        File file = allProperties()
                .addProperty(HANDLER, expected)
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertEquals(expected, configuration.serviceHandler());
    }


    @Test
    public void serviceHandler_withServiceHandler_empty() throws Exception {
        File file = allProperties()
                .addProperty(HANDLER, "")
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertEquals("/", configuration.serviceHandler());
    }


    @Test
    public void serviceHandler_withServiceHandler_withoutStartPrefix() throws Exception {
        String expected = "handler";
        File file = allProperties()
                .addProperty(HANDLER, expected)
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertEquals("/" + expected, configuration.serviceHandler());
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
    public void load_withIllegalPort_lessThenLegal() throws Exception {
        File file = allProperties()
                .addProperty(PORT, "1")
                .saveTo(temp.newFile());

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("less");
        assertNull(loadFromFile(file.getAbsolutePath()));
    }


    @Test
    public void load_withIllegalPort_greaterThenLegal() throws Exception {
        File file = allProperties()
                .addProperty(PORT, "7000000")
                .saveTo(temp.newFile());

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("greater");
        assertNull(loadFromFile(file.getAbsolutePath()));
    }


    @Test
    public void load_withIllegalPort_notNumber() throws Exception {
        File file = allProperties()
                .addProperty(PORT, "string")
                .saveTo(temp.newFile());

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("number");
        assertNull(loadFromFile(file.getAbsolutePath()));
    }


    @Test
    public void load_withIllegalPort_empty() throws Exception {
        File file = allProperties()
                .addProperty(PORT, "")
                .saveTo(temp.newFile());

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("number");
        assertNull(loadFromFile(file.getAbsolutePath()));
    }


    @Test
    public void servicePort_equalsServicePort() throws Exception {
        int expected = 54321;
        File file = allProperties()
                .addProperty(PORT, String.valueOf(expected))
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertEquals(expected, configuration.servicePort());
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
    public void emailTo_equalsEmailTo() throws Exception {
        String expected = "super@super.com";
        File file = allProperties()
                .addProperty(EMAIL_TO, expected)
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertEquals(expected, String.valueOf(configuration.emailTo()));
    }


    @Test
    public void emailFrom_equals_emailTo_whenLoad_withoutEmailFrom() throws Exception {
        File file = allProperties()
                .removeProperty(EMAIL_FROM)
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertEquals(configuration.emailTo(), configuration.emailFrom());
    }


    @Test
    public void emailFrom_equalsEmailFrom() throws Exception {
        String expected = "super@super.com";
        File file = allProperties()
                .addProperty(EMAIL_FROM, expected)
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertEquals(expected, String.valueOf(configuration.emailFrom()));
    }


    @Test
    public void emailAdmin_equals_emailTo_whenLoad_withoutEmailAdmin() throws Exception {
        File file = allProperties()
                .removeProperty(EMAIL_ADMIN)
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertEquals(configuration.emailTo(), configuration.emailAdmin());
    }


    @Test
    public void emailAdmin_equalsEmailAdmin() throws Exception {
        String expected = "super@super.com";
        File file = allProperties()
                .addProperty(EMAIL_ADMIN, expected)
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertEquals(expected, String.valueOf(configuration.emailAdmin()));
    }


    @Test
    public void serviceDebug_IsFalse_whenLoad_withoutServiceDebug() throws Exception {
        File file = allProperties()
                .removeProperty(DEBUG_SERVICE)
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertFalse(configuration.logger().isDebugEnabled());
    }


    @Test
    public void serviceDebug_isTrue_whenLoad_withServiceDebug_isTrue() throws Exception {
        File file = allProperties()
                .addProperty(DEBUG_SERVICE, "true")
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertTrue(configuration.logger().isDebugEnabled());
    }


    @Test
    public void serviceDebug_isFalse_whenLoad_withServiceDebug_isFalse() throws Exception {
        File file = allProperties()
                .addProperty(DEBUG_SERVICE, "false")
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertFalse(configuration.logger().isDebugEnabled());
    }


    @Test
    public void serviceDebug_isFalse_whenLoad_withServiceDebug_isSomeString() throws Exception {
        File file = allProperties()
                .addProperty(DEBUG_SERVICE, "string")
                .saveTo(temp.newFile());
        Configuration configuration = loadFromFile(file.getAbsolutePath());

        assertFalse(configuration.logger().isDebugEnabled());
    }


    @Test
    public void jettyDebug_isFalse_whenLoad_withoutJettyDebug() throws Exception {
        File file = allProperties()
                .removeProperty(DEBUG_JETTY)
                .saveTo(temp.newFile());
        assertNotNull(loadFromFile(file.getAbsolutePath()));

        assertFalse(Logger.getRootLogger().isDebugEnabled());
    }


    @Test
    public void jettyDebug_IsTrue_whenLoad_withJettyDebug_isTrue() throws Exception {
        File file = allProperties()
                .addProperty(DEBUG_JETTY, "true")
                .saveTo(temp.newFile());
        assertNotNull(loadFromFile(file.getAbsolutePath()));

        assertTrue(Logger.getRootLogger().isDebugEnabled());
    }


    @Test
    public void jettyDebug_IsFalse_whenLoad_withJettyDebug_isFalse() throws Exception {
        File file = allProperties()
                .addProperty(DEBUG_JETTY, "false")
                .saveTo(temp.newFile());
        assertNotNull(loadFromFile(file.getAbsolutePath()));

        assertFalse(Logger.getRootLogger().isDebugEnabled());
    }


    @Test
    public void jettyDebug_IsFalse_whenLoad_withJettyDebug_isSomeString() throws Exception {
        File file = allProperties()
                .addProperty(DEBUG_JETTY, "string")
                .saveTo(temp.newFile());
        assertNotNull(loadFromFile(file.getAbsolutePath()));

        assertFalse(Logger.getRootLogger().isDebugEnabled());
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