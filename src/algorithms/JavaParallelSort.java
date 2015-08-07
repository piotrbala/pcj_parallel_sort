package algorithms;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import algorithms.common.Utils;


public class JavaParallelSort {
    
    private static int[] numbers;
    
    private static void sort() {
        //NOTE pool is threads - 1 processors because 1 is used by main program
//        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(threads - 1));
//        System.out.println("Threads: " + ForkJoinPool.getCommonPoolParallelism());
        
        Arrays.parallelSort(numbers);
    }
    
    public static void main(String[] args) {
        numbers = new int[Utils.SIZE];
        long min = 0;
        System.out.println("Parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        for (int i = 0; i < 10; ++i) {
            Utils.randomize(numbers);
            long t = System.nanoTime();
            sort();
            t = System.nanoTime() - t;
            if (min > t || min == 0) {
                min = t;
            } 
        }
        System.out.println(min + " ns");
    }
}
