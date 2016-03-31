package partioning;


import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;


public class ConsistentHash<T> {

    private final HashFunction hashFunction;
    private final SortedMap<Integer, T> circle =
            new TreeMap<Integer, T>();

    public ConsistentHash(HashFunction hashFunction,
                          Collection<T> tokens) {

        this.hashFunction = hashFunction;

        for (T token : tokens) {
            add(token);
        }
    }

    public void add(T token) {
            circle.put(hashFunction.hash(token.toString()),
                    token);
    }

    public void remove(T token){
            circle.remove(hashFunction.hash(token.toString()));
    }

    public T get(String key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = hashFunction.hash(key);
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap =
                    circle.tailMap(hash);
            hash = tailMap.isEmpty() ?
                    circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

}