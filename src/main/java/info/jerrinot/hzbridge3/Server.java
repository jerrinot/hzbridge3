package info.jerrinot.hzbridge3;

import com.hazelcast.config.Config;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.core.Hazelcast;

public class Server {
    public static void main(String[] args) {
        Config config = new Config();
        config.addEventJournalConfig(new EventJournalConfig().setMapName("myMap").setEnabled(true));
        Hazelcast.newHazelcastInstance(config);
    }

}
