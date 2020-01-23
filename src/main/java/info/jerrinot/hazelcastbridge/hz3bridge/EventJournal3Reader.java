package info.jerrinot.hazelcastbridge.hz3bridge;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.internal.journal.EventJournalReader;
import com.hazelcast.map.journal.EventJournalMapEvent;
import com.hazelcast.ringbuffer.ReadResultSet;

public final class EventJournal3Reader<K, V> {
    private final HazelcastInstance client;
    private final com.hazelcast.internal.journal.EventJournalReader<EventJournalMapEvent<K, V>> reader;

    public EventJournal3Reader(String xmlConfig, String mapName) {
        client = HazelcastClient.newHazelcastClient(ConfigUtils.toConfig(xmlConfig));
        if (mapName != null) {
            reader = (EventJournalReader<EventJournalMapEvent<K, V>>) client.getMap(mapName);
        } else {
            reader = null;
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
