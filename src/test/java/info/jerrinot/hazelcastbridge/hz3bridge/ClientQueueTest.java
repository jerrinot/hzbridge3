package info.jerrinot.hazelcastbridge.hz3bridge;

public class ClientQueueTest {
    public static void main(String[] args) throws InterruptedException {
        QueueContextObject myQueue = new QueueContextObject("classpath:my-client-config.xml", "myQueue");
        for (;;) {
            byte[] take = myQueue.takeBytes();
            System.out.println(take);
        }
    }
}