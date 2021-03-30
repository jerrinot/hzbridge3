package info.jerrinot.hazelcastbridge.hz3bridge;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

public final class QueueContextObject<T> {
    private final HazelcastInstance client;
    private final IQueue<T> queue;

    public QueueContextObject(String xmlConfig, String queueName) {
        this.client = HazelcastClient.newHazelcastClient(ConfigUtils.toConfig(xmlConfig));
        this.queue = client.getQueue(queueName);
    }

    public T take() throws InterruptedException {
        return queue.take();
    }
}
