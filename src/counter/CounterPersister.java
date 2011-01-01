package counter;

/**
 * User: zahir
 * Date: 11-1-1
 * Time: PM12:49
 */
public interface CounterPersister {

    Long persist(Counter counter, long newCounter);

    Long getCounterValue(Counter counter);
}
