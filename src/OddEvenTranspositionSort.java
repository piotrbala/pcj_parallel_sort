import java.util.Arrays;
import java.util.Random;

import org.pcj.PCJ;
import org.pcj.Shared;
import org.pcj.StartPoint;
import org.pcj.Storage;


public class OddEvenTranspositionSort extends Storage implements StartPoint {

//    public final static int SIZE = 3 * 16 * 5 * 100 * 1000;
    public final static int SIZE = 3 * 4;
    
    
    @Shared
    private int[] receivedData;
    
    
    private int[] tmp;
    private int[] numbers;
    private Random r;
    
    public void randomizeNumbers() {
        for (int i = 0; i < numbers.length; ++i) {
            numbers[i] = r.nextInt();
        }
    }
    
    public void calculate() {
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
                int k = 0, l = 0;
                while (k + l < tmp.length) {
                    if (PhaseNeighbour[i % 2] < PCJ.myId())
                        tmp[k + l] = numbers[k] > receivedData[l] ? numbers[k++] : receivedData[l++];
                    else
                        tmp[k + l] = numbers[k] <= receivedData[l] ? numbers[k++] : receivedData[l++];
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
        numbers = new int[SIZE/PCJ.threadCount()];
        tmp = new int[SIZE/PCJ.threadCount()];
        r = new Random();
        
        long t = 0, min = 0;
        for (int i = 0; i < 10; ++i) {
            randomizeNumbers();
            PCJ.barrier();
            if (PCJ.myId() == 0)
                t = System.nanoTime();
            
            calculate();
            
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
