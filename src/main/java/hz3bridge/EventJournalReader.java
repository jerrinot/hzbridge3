package hz3bridge;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.map.journal.EventJournalMapEvent;
import com.hazelcast.ringbuffer.ReadResultSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class EventJournalReader<T> {
    private final HazelcastInstance client;
    private final String mapName;

    public EventJournalReader(String xmlConfig, String mapName) {
        try {
            Path tempFile = Files.createTempFile("client-config", "xml");
            Files.writeString(tempFile, xmlConfig);
            XmlClientConfigBuilder configBuilder = new XmlClientConfigBuilder(tempFile.toFile());
            ClientConfig config = configBuilder.build();
            client = HazelcastClient.newHazelcastClient(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.mapName = mapName;
    }

    public <K, V> ICompletableFuture<EventJournalInitialSubscriberState> subscribeToEventJournal(int partitionId) {
        com.hazelcast.internal.journal.EventJournalReader<EventJournalMapEvent<K, V>> map = (com.hazelcast.internal.journal.EventJournalReader<EventJournalMapEvent<K, V>>) client.getMap(mapName);
        ICompletableFuture<EventJournalInitialSubscriberState> eventJournalInitialSubscriberStateICompletableFuture = map.subscribeToEventJournal(partitionId);
        return eventJournalInitialSubscriberStateICompletableFuture;
    }

    public <K, V> ICompletableFuture<ReadResultSet<T>> readFromEventJournal(
            long startSequence,
            int minSize,
            int maxSize,
            int partitionId) {
        com.hazelcast.internal.journal.EventJournalReader<EventJournalMapEvent<K, V>> map = (com.hazelcast.internal.journal.EventJournalReader<EventJournalMapEvent<K, V>>) client.getMap(mapName);
        return map.readFromEventJournal(startSequence, minSize, maxSize, partitionId, null, null);
    }

    public void shutdown() {
        client.shutdown();
    }

    public int getPartitionCount() {
        HazelcastClientProxy clientProxy = (HazelcastClientProxy) client;
        return clientProxy.client.getClientPartitionService().getPartitionCount();
    }
}
