package algorithms;
import java.util.Arrays;

import org.pcj.PCJ;
import org.pcj.Shared;
import org.pcj.StartPoint;
import org.pcj.Storage;

import algorithms.common.Utils;


public class OddEvenTranspositionSort extends Storage implements StartPoint {
  
    @Shared
    private int[] receivedData;
    
    
    private int[] tmp;
    private int[] numbers;
    
    private void mergeWithHigher() {
        int k = 0, l = 0;
        while (k + l < tmp.length) {
            tmp[k + l] = numbers[k] <= receivedData[l] ? numbers[k++] : receivedData[l++];
        }
    }
    
    private void mergeWithLower() {
        int k = tmp.length - 1, l = tmp.length - 1;
        while (k + l >= tmp.length - 1) {
            tmp[k + l - tmp.length + 1] = numbers[k] > receivedData[l] ? numbers[k--] : receivedData[l--];
        }
    }
    
    private void sort() {
        int[] PhaseNeighbour = new int[2]; //oddPhase = PhaseNeighbour[1], evenPhase = PhaseNeighbour[0]
        if (PCJ.myId() % 2 == 0) {
            PhaseNeighbour[1] = PCJ.myId() - 1;
            PhaseNeighbour[0] = PCJ.myId() + 1;
        }
        else {
            PhaseNeighbour[1] = PCJ.myId() + 1;
            PhaseNeighbour[0] = PCJ.myId() - 1;
        }
        
        Arrays.sort(numbers);
        
        for (int i = 1; i < PCJ.threadCount() + 1; ++i) {
            if (PhaseNeighbour[i % 2] < PCJ.threadCount() && PhaseNeighbour[i % 2] >= 0) {
                PCJ.put(PhaseNeighbour[i % 2], "receivedData", numbers);
                PCJ.waitFor("receivedData");
                
                //choose
                if (PhaseNeighbour[i % 2] < PCJ.myId()) {
                    mergeWithLower();
                }
                else {
                    mergeWithHigher();
                }
                
                //swap
                int[] s = tmp;
                tmp = numbers;
                numbers = s;
            }
            
            PCJ.barrier();
        }
    }
    
    @Override
    public void main() throws Throwable {      
        numbers = new int[Utils.SIZE/PCJ.threadCount()];
        tmp = new int[Utils.SIZE/PCJ.threadCount()];
        
        long t = 0, min = 0;
        for (int i = 0; i < 10; ++i) {
            Utils.randomize(numbers);
            PCJ.barrier();
            if (PCJ.myId() == 0)
                t = System.nanoTime();
            
            sort();
            
            if (PCJ.myId() == 0) {
                t = System.nanoTime() - t;
                if (min > t || min == 0) {
                    min = t;
                }
            }
        }
        if (PCJ.myId() == 0) {
            System.out.println(min + " ns");
        }
//        PCJ.log(Arrays.toString(numbers));
    }

}
