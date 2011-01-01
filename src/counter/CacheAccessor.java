package counter;

/**
 * User: zahir
 * Date: 11-1-1
 * Time: PM12:46
 */
public interface CacheAccessor {


    boolean set(String key, Object val, long expire);

    Object get(String key);

    <T> T get(String key, Class<T> type);

    boolean remove(String key);
}
