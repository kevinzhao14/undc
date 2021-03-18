package dungeoncrawler.handlers;
import java.util.HashMap;

/**
 * Bi-directional Map for storing mouse button to function key relationships.
 * @param <K> Object type for keys
 * @param <V> Object type for values
 */
class BiMap<K, V> {

    private HashMap<K, V> map = new HashMap<>();
    private HashMap<V, K> inversedMap = new HashMap<>();

    /**
     * Constructor for a Bi-Map.
     * @param keys Array of Keys to load
     * @param values Array of Values to load
     */
    BiMap(K[] keys, V[] values) {
        for (int i = 0; i < keys.length; i++) {
            put(keys[i], values[i]);
        }
    }

    /**
     * Puts the key-value pair into the map.
     * @param k Key data
     * @param v Value data
     */
    void put(K k, V v) {
        map.put(k, v);
        inversedMap.put(v, k);
    }

    /**
     * Returns a Value from a Key.
     * @param k Key object to search for
     * @return Returns the associated value object
     */
    V get(K k) {
        return map.get(k);
    }

    /**
     * Returns a Key from a Value.
     * @param v Value object to search for
     * @return Returns the associated Key
     */
    K getKey(V v) {
        return inversedMap.get(v);
    }
}