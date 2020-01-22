package hz3bridge;

import com.hazelcast.client.HazelcastClient;

public class Foo {
    public static void foo() {
        HazelcastClient.newHazelcastClient();
    }
}
