import iterators.IntArrayIterator;
import iterators.SimplifiedArrayIterator;
import iterators.WaitingArrayIterator;

import java.util.Arrays;
import java.util.Random;

import org.pcj.PCJ;
import org.pcj.Shared;
import org.pcj.StartPoint;
import org.pcj.Storage;

import tools.PcjTools;


public class MergeSort extends Storage implements StartPoint{
    
    public final static int SEND_SIZE = 64_000;
//    public final static int SEND_SIZE = 3;
    
    
    public final static int SIZE = 3 * 16 * 15 * SEND_SIZE;
//    public final static int SIZE = 8 * SEND_SIZE;
    
    @Shared
    private int[][] child1;
    
    @Shared
    private int[][] child2;
    
    private int[] numbers;
    private Random r;
    
    
    
    public void randomizeNumbers() {
        for (int i = 0; i < numbers.length; ++i) {
            numbers[i] = r.nextInt();
        }
    }
    
    public void calculate() {
        
        Arrays.sort(numbers); //quick sort
        
        //building a tree + communication
        int[] children = PcjTools.getChildren(PCJ.myId(), PCJ.threadCount());
        
        switch (children.length) {
        case 2:
            PCJ.waitFor("child2");
            PCJ.waitFor("child1");
            break;
        case 1:
            PCJ.waitFor("child1");
            child2 = new int[0][];
            break;
        case 0:
            child1 = new int[0][];
            child2 = new int[0][];
            break;
        }
        
        int size1 = child1.length;
        int size2 = child2.length;

        int totalSize = size1 + size2 + (SIZE / (PCJ.threadCount() * SEND_SIZE));
        
        int parent = PcjTools.getParent(PCJ.myId(), PCJ.threadCount());

        String parentFieldName = null;
        if (PCJ.myId() != 0) {
            parentFieldName = PcjTools.getChildren(parent, PCJ.threadCount())[0] == PCJ.myId() ?
                "child1" : "child2";
            PCJ.put(parent, parentFieldName, new int[totalSize][]);
        }
        
        //no waiting for data. Iterators will wait when necessary

        //merging
        IntArrayIterator it1 = new WaitingArrayIterator(child1, SEND_SIZE, "child1");
        it1.move();
        IntArrayIterator it2 = new WaitingArrayIterator(child2, SEND_SIZE, "child2");
        it2.move();
        IntArrayIterator it3 = new SimplifiedArrayIterator(numbers);
        it3.move();
        IntArrayIterator smallest;
        
        
        int[] result = new int[SEND_SIZE];
        for (int i = 0; i < totalSize; ++i) { 
            for (int j = 0; j < result.length; ++j) {
                smallest = it1.lesser(it2.lesser(it3));
                result[j] = smallest.get();
                smallest.move();
            }

            if (PCJ.myId() != 0)
                PCJ.put(parent, parentFieldName, result, i);
            /*else {
                System.out.println(Arrays.toString(result));
            }*/
        }
    }
    
    @Override
    public void main() throws Throwable {
        numbers = new int[SIZE/PCJ.threadCount()];
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
    }
}
