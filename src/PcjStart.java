
import java.util.Arrays;

import org.pcj.PCJ;

import algorithms.NaiveMergeSort;
import algorithms.OddEvenTranspositionSort;


public class PcjStart {
    
    /**
     * Method useful for testing on local machine;
     * @param threadCount number of threads to start
     * @return Array with "localhost" repeated threadCount times.
     */
    private static String[] nodes(int threadCount) {
        String[] result = new String[threadCount];
        Arrays.fill(result, "localhost");
        return result;
    }
    
    public static void main(String[] args) {
        //it will look for file "nodes.file" and read nodes configuration from there
        PCJ.start(NaiveMergeSort.class, NaiveMergeSort.class);
        
//        PCJ.deploy(NaiveMergeSort.class, NaiveMergeSort.class, nodes(4));
    }
}
