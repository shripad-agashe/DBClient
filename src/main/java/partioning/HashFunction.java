package partioning;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class HashFunction {
    public Integer hash(String input){

        com.google.common.hash.HashFunction hf = Hashing.murmur3_32();
        Hasher hasher = hf.newHasher();
        hasher.putBytes(input.getBytes());
        return  hasher.hash().asInt();
    }
}
