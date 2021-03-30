package info.jerrinot.hazelcastbridge.hz3bridge;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueTakeCodec;
import com.hazelcast.client.proxy.ClientQueueProxy;
import com.hazelcast.core.HazelcastInstance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class QueueContextObject {
    private final HazelcastInstance client;
    private final ClientQueueProxy<?> queue;
    private final String name;

    private static Method INVOKE_ON_PARTITION_METHOD = getMethod();

    private static Method getMethod() {
        try {
            Method invokeOnPartitionInterruptibly = ClientQueueProxy.class.getSuperclass().getDeclaredMethod("invokeOnPartitionInterruptibly", ClientMessage.class);
            if (!invokeOnPartitionInterruptibly.isAccessible()) {
                invokeOnPartitionInterruptibly.setAccessible(true);
            }
            return invokeOnPartitionInterruptibly;
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    public QueueContextObject(String xmlConfig, String queueName) {
        this.client = HazelcastClient.newHazelcastClient(ConfigUtils.toConfig(xmlConfig));
        this.queue = (ClientQueueProxy<?>) client.getQueue(queueName);
        this.name = queueName;
    }

    public byte[] takeBytes() throws InterruptedException {
        ClientMessage clientMessage = QueueTakeCodec.encodeRequest(name);
        try {
            ClientMessage response = (ClientMessage) INVOKE_ON_PARTITION_METHOD.invoke(queue, clientMessage);
            QueueTakeCodec.ResponseParameters resultParameters = QueueTakeCodec.decodeResponse(response);
            return resultParameters.response.toByteArray();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}