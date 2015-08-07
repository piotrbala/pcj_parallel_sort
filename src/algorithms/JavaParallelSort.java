package algorithms;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;


public class JavaParallelSort {

    public final static int SIZE = 3 * 16 * 5 * 100 * 1000;
    
    private static int[] numbers;
    private static Random r;
    
    public static void randomizeNumbers() {
        for (int i = 0; i < numbers.length; ++i) {
            numbers[i] = r.nextInt();
        }
    }
    
    public static void calculate() {
        //NOTE pool is threads - 1 processors because 1 is used by main program
//        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(threads - 1));
//        System.out.println("Threads: " + ForkJoinPool.getCommonPoolParallelism());
        
        Arrays.parallelSort(numbers);
    }
    
    public static void main(String[] args) {
        numbers = new int[SIZE];
        r = new Random();
        long min = 0;
        System.out.println("Parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        for (int i = 0; i < 10; ++i) {
            randomizeNumbers();
            long t = System.nanoTime();
            calculate();
            t = System.nanoTime() - t;
            if (min > t || min == 0) {
                min = t;
            } 
        }
        System.out.println(min + " ns");
    }
}
