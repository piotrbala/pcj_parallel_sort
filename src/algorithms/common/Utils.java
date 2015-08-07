package algorithms.common;

import java.util.Random;

public class Utils {
    /**
     * It is SIZE of data to sort.
     */
//    public final static int SIZE = 46_080_000;
    public final static int SIZE = 3 * 4;
    
    
    /**
     * Fills array with random numbers.
     */
    public static void randomize(int[] array) {
        Random r = new Random();
        for (int i = 0; i < array.length; ++i) {
            array[i] = r.nextInt();
        }
    }
    
}
