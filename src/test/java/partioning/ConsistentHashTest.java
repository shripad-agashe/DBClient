package partioning;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ConsistentHashTest {

    @Test
    public void testAddForE() throws Exception {

        List<Token> strings = Arrays.asList(new Token("1"));
        ConsistentHash<Token> consistentHash = new ConsistentHash<Token>(new HashFunction(), strings);
        Token e = consistentHash.get("E");
        System.out.println(e.getPosition());

    }

    @Test
    public void testAddForEFor30Replica() throws Exception {

        List<Token> strings = Arrays.asList(new Token("1"));
        ConsistentHash<Token> consistentHash = new ConsistentHash<Token>(new HashFunction(), strings);
        Token e = consistentHash.get("E");
        System.out.println(e.getPosition());

    }
}