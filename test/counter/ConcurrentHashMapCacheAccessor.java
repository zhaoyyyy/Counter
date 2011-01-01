package counter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: zahir
 * Date: 11-1-1
 * Time: PM1:25
 * To change this template use File | Settings | File Templates.
 */
public class ConcurrentHashMapCacheAccessor implements CacheAccessor {

    private ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>();

    public void clearCache() {
        cache.clear();
    }

    public boolean set(String key, Object val, long expire) {
        cache.put(key, val);
        return true;
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        return (T) cache.get(key);
    }

    public boolean remove(String key) {
        cache.remove(key);
        return true;
    }
}
