package hz3bridge;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.internal.journal.EventJournalReader;
import com.hazelcast.map.journal.EventJournalMapEvent;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.ringbuffer.ReadResultSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class EventJournal3Reader<K, V> {
    private final HazelcastInstance client;
    private final com.hazelcast.internal.journal.EventJournalReader<EventJournalMapEvent<K, V>> reader;

    public EventJournal3Reader(String xmlConfig, String mapName) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("client-config", ".xml");
            Files.writeString(tempFile, xmlConfig);
            XmlClientConfigBuilder configBuilder = new XmlClientConfigBuilder(tempFile.toFile());
            ClientConfig config = configBuilder.build();
            client = HazelcastClient.newHazelcastClient(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            deleteQuietly(tempFile);
        }
        if (mapName != null) {
            reader = (EventJournalReader<EventJournalMapEvent<K, V>>) client.getMap(mapName);
        } else {
            reader = null;
        }
    }

    private void deleteQuietly(Path path) {
        if (path != null) {
            IOUtil.deleteQuietly(path.toFile());
        }
    }

    public ICompletableFuture<EventJournalInitialSubscriberState> subscribeToEventJournal(int partitionId) {
        return reader.subscribeToEventJournal(partitionId);
    }

    public ICompletableFuture<ReadResultSet<EventJournalMapEvent<K, V>>> readFromEventJournal(
            long startSequence,
            int minSize,
            int maxSize,
            int partitionId) {
        return reader.readFromEventJournal(startSequence, minSize, maxSize, partitionId, null, null);
    }

    public void shutdown() {
        client.shutdown();
    }

    public int getPartitionCount() {
        HazelcastClientProxy clientProxy = (HazelcastClientProxy) client;
        return clientProxy.client.getClientPartitionService().getPartitionCount();
    }
}
