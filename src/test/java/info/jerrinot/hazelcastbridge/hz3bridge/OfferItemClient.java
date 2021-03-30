package info.jerrinot.hazelcastbridge.hz3bridge;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.util.UuidUtil;

public class OfferItemClient {
    public static void main(String[] args) throws InterruptedException {
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().addAddress("localhost:3120");
        HazelcastInstance client = HazelcastClient.newHazelcastClient(config);

        IQueue<String> myQueue = client.getQueue("myQueue");
        for (;;) {
            myQueue.offer(UuidUtil.newSecureUuidString());
            Thread.sleep(1000);
        }
    }
}
