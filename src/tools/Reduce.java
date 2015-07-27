package tools;

import java.io.Serializable;


/**
 * Reduce is interface to reduce data in threads. Complete result will be present in thread with id 0.
 * @param <A> accumulator. It may be send to other threads.
 * @param <V> value type. Data to reduce.
 */
public abstract class Reduce<A extends Serializable, V> {

    /**
     * Uses given function and data from children to calculate new accumulator. It is blocking operation.
     * It synchronizes all threads.
     * @param value is passed to given function
     * @return current accumulator. Only in master node it will return 
     * the result of reduction of data from all threads.
     */
    public abstract A calculate(V value);
}
