package hz3bridge;

import com.hazelcast.client.config.ClientConfig;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConfigUtilsTest {
    private static final String CLIENT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<hazelcast-client xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "                  xsi:schemaLocation=\"http://www.hazelcast.com/schema/client-config\n"
            + "                               http://www.hazelcast.com/schema/client-config/hazelcast-client-config-3.11.xsd\"\n"
            + "                  xmlns=\"http://www.hazelcast.com/schema/client-config\">\n"
            + "    <network>\n"
            + "        <cluster-members>\n"
            + "            <address>127.0.0.1:571</address>\n"
            + "        </cluster-members>\n"
            + "    </network>\n"
            + "    <group>\n"
            + "        <name>dev</name>\n"
            + "        <password>1234</password>\n"
            + "    </group>\n"
            + "</hazelcast-client>\n";

    @Test
    public void fromXmlLiteral() {
        ClientConfig clientConfig = ConfigUtils.toConfig(CLIENT_XML);
        assertTrue(clientConfig.getNetworkConfig().getAddresses().contains("127.0.0.1:571"));
    }

    @Test
    public void fromClasspath() {
        ClientConfig clientConfig = ConfigUtils.toConfig("classpath:my-client-config.xml");
        assertTrue(clientConfig.getNetworkConfig().getAddresses().contains("127.0.0.1:999"));
    }
}
