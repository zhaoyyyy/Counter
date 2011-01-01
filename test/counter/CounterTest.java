package counter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: zahir
 * Date: 11-1-1
 * Time: PM1:24
 * To change this template use File | Settings | File Templates.
 */
public class CounterTest extends Assert {

    private ConcurrentHashMapCacheAccessor ca;
    private HashMapCounterPersister cp;

    Counter counter;


    @Before
    public void setUp() throws Exception {
        ca = new ConcurrentHashMapCacheAccessor();
        cp = new HashMapCounterPersister();
        counter = new Counter(
                ca, cp, "counter", "counter", "a_counter"
        );
    }

    @Test
    public void testClickNonExistsCounter() {

        ca.clearCache();

        long result = click(counter);

        assertEquals(1l, result);

        assertCacheCounterEquals(1l, counter);

        assertNull( cp.getCounterValue(counter) );
    }

    @Test
    public void testClickExistsCounterButNotReachFlushSize() throws InterruptedException {

        ca.clearCache();

        counter.setMinAvailableTime(3);

        for (int i = 0; i < counter.getThreshold() - 1; i++) {
            Thread.sleep(counter.getMinAvailableTime() + 1);
            click(counter);
        }

        assertEquals(counter.getThreshold() - 1, counter.getCacheCount().longValue());

        assertNull(counter.getPersistCount());

    }

    @Test
    public void testMinAvailableTimeWorks() {

        ca.clearCache();

        counter.setMinAvailableTime(3000);//3s

        click(counter);
        click(counter);
        click(counter);
        click(counter);

        assertCacheCounterEquals(1l, counter);
    }

    @Test
    public void testCounterDidPersistWhenReachFlushSize() {

        ca.clearCache();
        counter.setMinAvailableTime(0);

        counter.increase();//init counter cache
        counter.increaseBy(counter.getThreshold());

        //cache should be 0
        assertEquals(0l, counter.getCacheCount().longValue());
        // and db should be threshold + 1
        assertEquals(counter.getThreshold() + 1, counter.getPersistCount().longValue());

    }


    private void assertCacheCounterEquals(Long val, Counter c) {
        assertNotNull(c.getCacheCount());
        assertEquals(val, c.getCacheCount());
    }


    private long click(Counter c) {
        return c.increase();
    }

}
