package counter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: zahir
 * Date: 11-1-1
 * Time: PM1:28
 * To change this template use File | Settings | File Templates.
 */
public class HashMapCounterPersister implements CounterPersister{

    private ConcurrentHashMap<String, Long> data = new ConcurrentHashMap<String, Long>();


    public Long persist(Counter counter, long newCounter) {

        Long old = data.get(counter.getPersistCounterName());

        if ( old == null ) old = 0l;

        data.put(counter.getPersistCounterName(), old + newCounter);

        return old + newCounter;
    }

    public Long getCounterValue(Counter counter) {
        return data.get(counter.getPersistCounterName());
    }

}
