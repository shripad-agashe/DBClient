package partioning;

import org.junit.Test;

public class HashFunctionTest {

    @Test
    public void checkHashValue(){
        HashFunction hashFunction = new HashFunction();
        System.out.println(hashFunction.hash("Aa"));
        System.out.println(hashFunction.hash("A"));
        System.out.println(hashFunction.hash("A1"));
        System.out.println(hashFunction.hash("B"));
        System.out.println(hashFunction.hash("B1"));
    }

}