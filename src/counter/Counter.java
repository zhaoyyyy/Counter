package counter;

/**
 * User: zahir
 * Date: 11-1-1
 * Time: PM12:50
 */
public class Counter {

    private CacheAccessor cacheAccessor;
    private CounterPersister counterPersister;

    /** 该counter的名称，必须唯一, 用于决定缓存的key */
    private String name;

    /** 缓存中存贮上次修改时间的key */
    private String lastModifiedKey;

    /** 有效点击间隔时间 */
    private long minAvailableTime;

    /** 缓存失效时间 */
    private long expire;

    /** 缓存中的存贮点击量的最大值 */
    private long flushSize;

    /** 持久化点击量时的counter名称(例如 数据库表名称) */
    private String persistCounterName;

    /** 持久化点击量时，点击量所在列的名称(例如 数据库列名称) */
    private String persistCounterValueName;

    public Counter(CacheAccessor cacheAccessor, CounterPersister counterPersister, String persistCounterName, String counterValueName) {
        this.cacheAccessor = cacheAccessor;
        this.counterPersister = counterPersister;
        this.name = persistCounterName + "#" + counterValueName;
        this.persistCounterName = this.persistCounterName;
        this.persistCounterValueName = counterValueName;

        this.lastModifiedKey = "lastModified_" + name;
        this.minAvailableTime = 1000;
        this.flushSize = 15;
    }

    public Long getCacheCount() {
        return cacheAccessor.get( getCounterKey(), Long.class );
    }

    public Long getPersistCount() {
        return counterPersister.getCounterValue(this);
    }

    public Long getTotalCount() {
        Long cacheCount = getCacheCount();
        Long persistCount = getPersistCount();
        if ( cacheCount == null ) cacheCount = 0l;
        if ( persistCount == null ) persistCount = 0l;
        return cacheCount + persistCount;
    }

    public Counter(CacheAccessor cacheAccessor, String name, String lastModifiedKey, long minAvailableTime, String counterName, String databaseColumnName) {
        this.cacheAccessor = cacheAccessor;
        this.name = name;
        this.lastModifiedKey = lastModifiedKey;
        this.minAvailableTime = minAvailableTime;
        this.persistCounterName = counterName;
        this.persistCounterValueName = databaseColumnName;
    }

    public synchronized Long increaseBy(long n) {
        Long counter = getCacheCount();

        if (!available()) {
            return counter;
        }

        if ( counter == null ) {//XXX
            cacheAccessor.set(getCounterKey(), n, expire);
            return n;
        }

        if (needFlush(counter + n)) {
            return persist(counter + n);
        }

        cacheAccessor.set(getCounterKey(), counter + n, expire);
        cacheAccessor.set(lastModifiedKey, System.currentTimeMillis(), expire);
        return counter + n;
    }

    public synchronized Long increase() {
        return increaseBy(1l);
    }

    public synchronized long decrease() {
        Long counter = cacheAccessor.get(getCounterKey(), Long.class);
        if (!available()) {
            return counter;
        }

        if (counter == null) {
            return 0;
        }

        cacheAccessor.set(getCounterKey(), --counter, expire);
        nowAsLastModified();
        return counter;
    }

    private long persist(long counter) {
        counterPersister.persist(this, counter);
        cacheAccessor.set(getCounterKey(), 0l, expire);
        nowAsLastModified();
        return 0;
    }

    private long nowAsLastModified() {
        long now = System.currentTimeMillis();
        cacheAccessor.set(lastModifiedKey, now, expire);
        return now;
    }

    private boolean needFlush(Long counter) {
        return counter >= flushSize;
    }

    private boolean available() {
        Long lastModified = cacheAccessor.get(lastModifiedKey, Long.class);

        if ( lastModified == null ) {
            lastModified = nowAsLastModified();
        }

        return (System.currentTimeMillis() - lastModified) >= minAvailableTime ;
    }

    private String getCounterKey() {
        return "counter_" + name;
    }


    public CacheAccessor getCacheAccessor() {
        return cacheAccessor;
    }

    public void setCacheAccessor(CacheAccessor cacheAccessor) {
        this.cacheAccessor = cacheAccessor;
    }

    public CounterPersister getCounterPersister() {
        return counterPersister;
    }

    public void setCounterPersister(CounterPersister counterPersister) {
        this.counterPersister = counterPersister;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastModifiedKey() {
        return lastModifiedKey;
    }

    public void setLastModifiedKey(String lastModifiedKey) {
        this.lastModifiedKey = lastModifiedKey;
    }

    public long getMinAvailableTime() {
        return minAvailableTime;
    }

    public void setMinAvailableTime(long minAvailableTime) {
        this.minAvailableTime = minAvailableTime;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public long getFlushSize() {
        return flushSize;
    }

    public void setFlushSize(long flushSize) {
        this.flushSize = flushSize;
    }

    public String getPersistCounterName() {
        return persistCounterName;
    }

    public void setPersistCounterName(String persistCounterName) {
        this.persistCounterName = persistCounterName;
    }

    public String getPersistCounterValueName() {
        return persistCounterValueName;
    }

    public void setPersistCounterValueName(String persistCounterValueName) {
        this.persistCounterValueName = persistCounterValueName;
    }
}
