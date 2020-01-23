package info.jerrinot.hazelcastbridge.hz3bridge;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.nio.IOUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class ConfigUtils {
    private static final String CLASSPATH_PREFIX = "classpath:";

    public static ClientConfig toConfig(String xmlConfig) {
        ClientConfig clientConfig = tryConfigResource(xmlConfig);
        if (clientConfig != null) {
            return clientConfig;
        }
        return xmlToConfig(xmlConfig);
    }

    private static ClientConfig tryConfigResource(String xmlConfig) {
        if (xmlConfig.startsWith(CLASSPATH_PREFIX)) {
            String configLocation = xmlConfig.substring(CLASSPATH_PREFIX.length());
            return localFromResource(configLocation);
        } else {
            return null;
        }
    }

    private static ClientConfig localFromResource(String configLocation) {
        try {
            XmlClientConfigBuilder builder = new XmlClientConfigBuilder(configLocation);
            return builder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ClientConfig xmlToConfig(String xmlConfig) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("client-config", ".xml");
            Files.writeString(tempFile, xmlConfig);
            XmlClientConfigBuilder configBuilder = new XmlClientConfigBuilder(tempFile.toFile());
            return configBuilder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            deleteQuietly(tempFile);
        }
    }

    private static void deleteQuietly(Path path) {
        if (path != null) {
            IOUtil.deleteQuietly(path.toFile());
        }
    }
}
