package partioning;

import org.junit.Test;

import java.util.Arrays;

public class ConsistentHashTest {

    @Test
    public void testAddForE() throws Exception {

        ConsistentHash<String> consistentHash = new ConsistentHash<String>(new HashFunction(),3,  Arrays.asList("A", "B", "C", "D"));
        String e = consistentHash.get("E");
        System.out.println(e);

    }

    @Test
    public void testAddForEFor30Replica() throws Exception {

        ConsistentHash<String> consistentHash = new ConsistentHash<String>(new HashFunction(),9,  Arrays.asList("A", "B", "C", "D"));
        String e = consistentHash.get("E");
        System.out.println(e);

    }
}