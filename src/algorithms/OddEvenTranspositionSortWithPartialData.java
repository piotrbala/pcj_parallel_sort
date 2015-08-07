package algorithms;

import java.util.Arrays;

import org.pcj.PCJ;
import org.pcj.Shared;
import org.pcj.StartPoint;
import org.pcj.Storage;

import algorithms.common.Utils;

/**
 * This is the OddEvenTranspositionSort with 1. proposed improvement.
 */
public class OddEvenTranspositionSortWithPartialData extends Storage implements StartPoint {
  
    @Shared
    private int[] numbers;
  
  
    private int[] tmp;
    private int myId, threadCount;

    private void mergeWithHigher(int neighbour) {
        //we are only interested in small ones
        int k = 0, l = 0;
        while (k + l < tmp.length) {
            int number = PCJ.get(neighbour, "numbers", l);
            if (numbers[k] < number) {
                tmp[k + l] = numbers[k++];
            }
            else {
                tmp[k + l] = number;
                l++;
            }
        }
    }
    
    private void mergeWithLower(int neighbour) {
        //we are only interested in big ones
        int k = numbers.length - 1, l = numbers.length - 1;
        while (k + l >= tmp.length - 1) {
            int number = PCJ.get(neighbour, "numbers", k);
            if (number < numbers[l]) {
                tmp[k + l - tmp.length + 1] = numbers[l--];
            }
            else {
                tmp[k + l - tmp.length + 1] = number;
                k--;
            }
        }
    }
    
    private void sort() {
        int[] PhaseNeighbour = new int[2]; //oddPhase = PhaseNeighbour[1], evenPhase = PhaseNeighbour[0]
        if (PCJ.myId() % 2 == 0) {
            PhaseNeighbour[1] = myId - 1;
            PhaseNeighbour[0] = myId + 1;
        }
        else {
            PhaseNeighbour[1] = myId + 1;
            PhaseNeighbour[0] = myId - 1;
        }
  
        Arrays.sort(numbers);
  
        for (int phase = 1; phase < threadCount + 1; ++phase) {
            if (PhaseNeighbour[phase % 2] < threadCount && PhaseNeighbour[phase % 2] >= 0) {
                //ensure that neighbour finished previous calculations
                PCJ.barrier(PhaseNeighbour[phase % 2]);

                //we try to avoid sending too much data
                if (PhaseNeighbour[phase % 2] < myId)
                    mergeWithLower(PhaseNeighbour[phase % 2]);
                else
                    mergeWithHigher(PhaseNeighbour[phase % 2]);
  
                //so we can securely cover previous data
                PCJ.barrier(PhaseNeighbour[phase % 2]);
  
                //swap
                int[] s = tmp;
                tmp = numbers;
                numbers = s;
            }
        }
    }
  
    @Override
    public void main() throws Throwable {
        myId = PCJ.myId();
        threadCount = PCJ.threadCount();
      
        numbers = new int[Utils.SIZE/threadCount];
        tmp = new int[Utils.SIZE/threadCount];
      
      
        long t = 0, min = 0;
        for (int i = 0; i < 10; ++i) {
            Utils.randomize(numbers);
            PCJ.barrier();
            if (myId == 0)
                t = System.nanoTime();
          
            sort();
          
            if (myId == 0) {
                t = System.nanoTime() - t;
                if (min > t || min == 0) {
                    min = t;
                }
            }
        }
        if (myId == 0) {
            System.out.println(min + " ns");
        }
//        PCJ.log(Arrays.toString(numbers));
    }
}
