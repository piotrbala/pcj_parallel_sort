package tools;

import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.pcj.PCJ;

import tools.Function;

public class PcjTools {

    /**
     * It returns class that can reduce data in threads. Complete result will be present in thread with id 0.
     * It has to be used in all threads.
     * @param function used to reduce data. It takes 3 args - 2 accumulators from children and a value
     * for calculations. It returns accumulator.
     * @param <A> accumulator. It may be send to other threads.
     * @param <V> value type. Data to reduce.
     * @param defaultAccumulator it will be passed to function iff this thread does not have child
     * @param metaField name of the field in the storage that will be used to store meta-data. It has to be A
     * and has to be null. This field has to have the same name in each thread.
     */
    public static <A extends Serializable, V> Reduce<A, V>
        prepareTreeReduce(Function<A, A, V, A> function, A defaultAccumulator, String metaField) {
        
        return new TreeReduce<A, V>(function, defaultAccumulator, metaField);
    }
    
   
    /**
     * It returns class that can reduce data in threads. Complete result will be present in thread with id 0.
     * It has to be used in all threads.
     * @param function used to reduce data. It takes 2 args - accumulator from other thread and a value
     * for calculations. It returns accumulator.
     * @param <A> accumulator. It may be send to other threads.
     * @param <V> value type. Data to reduce.
     * @param defaultAccumulator it will be passed to function iff this thread does not have right neighbour
     * @param metaField name of the field in the storage that will be used to store meta-data. It has to be A
     * and has to be null. This field has to have the same name in each thread.
     */
    public static <A extends Serializable, V> Reduce<A, V>
        prepareLineReduce(BiFunction<A, V, A> function, A defaultAccumulator, String metaField) {
    
        return new LineReduce<A, V>(function, defaultAccumulator, metaField);
    }
    
    /**
     * It is waiting until field in given thread is in given condition
     * @param thread where the field is stored
     * @param field to get
     * @param predicate used to test field
     * @return value of field when it satisfies the predicate
     */
    public static <T> T waitForOuter(int thread, String field, Predicate<T> predicate) {
        T tmp;
        while (!predicate.test(tmp = PCJ.get(thread, field)));
        return tmp;
    }
    
    /**
     * Barrier on all given threads.
     * @param threads to synchronize. It has to contain caller thread (PCJ.myId())
     */
    public static void barrier(int... threads) {
        int myId = 0;
        while(threads[myId] != PCJ.myId()) {
            ++myId;
        }
        int[] children = getChildren(myId, threads.length);
        int parent = getParent(myId, threads.length);
        
        //synchronize
        for(int i : children) {
            PCJ.barrier(threads[children[i]]);
        }
        PCJ.barrier(threads[parent]);
        
        //to ensure that all threads reached barrier
        if (myId != threads[0]) {
            PCJ.barrier(threads[parent]);
        }
        for(int i : children) {
            PCJ.barrier(threads[children[i]]);
        }
    }
    
    /**
     * It will calculate children of given thread in given context.
     * threadCount doesn't have to be equal PCJ.threadCount() 
     * @param thread 
     * @param threadCount
     * @return array of children of given thread
     */
    public static int[] getChildren(int thread, int threadCount) {
        int number = (thread + 1) * 2 - 1;
        
        if (number >= threadCount)
            return new int[0];
        else if (number + 1 >= threadCount)
            return new int[]{number};
        else
            return new int[]{number, number + 1};
    }
    
    /**
     * Calculates a parent of given thread in given context.
     * @param thread
     * @param threadCount
     * @return parent of thread in given context. Thread 0 has no parent so -1 will be returned.
     */
    public static int getParent(int thread, int threadCount) {
        return (thread + 1) / 2 - 1;
    }
    
}
