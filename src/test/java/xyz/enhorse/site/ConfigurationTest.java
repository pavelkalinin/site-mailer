package xyz.enhorse.site;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static xyz.enhorse.site.Configuration.loadFromFile;
import static xyz.enhorse.site.ConfigurationProducer.FULL_CONFIG;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         12.08.2016
 */
public class ConfigurationTest {

    private static TemporaryFolder temp;


    @Test
    public void toString_output() throws Exception {
        File file = temp.newFile();
        FULL_CONFIG.saveTo(file);
        Configuration config = loadFromFile(file.getAbsolutePath());
        String string = config.toString();

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