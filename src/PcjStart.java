
import java.util.Arrays;

import org.pcj.PCJ;


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
//        for (int i = 0; i < 10; ++i)
//            PCJ.deploy(MergeSort.class, MergeSort.class, "nodes.txt");
        
        PCJ.deploy(OddEvenTranspositionSort.class, OddEvenTranspositionSort.class, nodes(4));
    }
}
