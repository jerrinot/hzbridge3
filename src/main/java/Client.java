import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient();
        IMap<Integer, String> myMap = hazelcastInstance.getMap("myMap");
        for (;;) {
            myMap.put(ThreadLocalRandom.current().nextInt(1000), UUID.randomUUID().toString());
            Thread.sleep(1000);
        }
    }
}
