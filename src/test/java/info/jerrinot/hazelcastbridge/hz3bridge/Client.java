package info.jerrinot.hazelcastbridge.hz3bridge;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("localhost:3120");
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
        IMap<Integer, String> myMap = hazelcastInstance.getMap("myMap");
        for (;;) {
            myMap.put(ThreadLocalRandom.current().nextInt(1000), UUID.randomUUID().toString());
            Thread.sleep(1000);
        }
    }
}
