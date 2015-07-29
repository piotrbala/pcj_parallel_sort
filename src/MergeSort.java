import java.util.Arrays;
import java.util.Random;

import org.pcj.PCJ;
import org.pcj.Shared;
import org.pcj.StartPoint;
import org.pcj.Storage;

import tools.PcjTools;


public class MergeSort extends Storage implements StartPoint{
    
    public final static int SIZE = 3 * 16 * 5 * 100 * 1000;
//    public final static int SIZE = 3 * 4;

    @Shared
    private int[][] child1;
    
    @Shared
    private int[][] child2;
    
    private int[] numbers;
    private Random r;
    
    private int[][] result; //only for 0 thread
    
    
    
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

        int totalSize = size1 + size2 + 1;
        
        int parent = PcjTools.getParent(PCJ.myId(), PCJ.threadCount());

        String parentFieldName = null;
        if (PCJ.myId() == 0) {
            result = new int[totalSize][];
        }
        else {
            parentFieldName = PcjTools.getChildren(parent, PCJ.threadCount())[0] == PCJ.myId() ?
                "child1" : "child2";
            PCJ.put(parent, parentFieldName, new int[totalSize][]);
        }
        
        
        PCJ.waitFor("child1", size1);
        PCJ.waitFor("child2", size2);

        //merging
        IntArrayIterator it1 = new IntArrayIterator(child1, numbers.length);
        IntArrayIterator it2 = new IntArrayIterator(child2, numbers.length);
        IntArrayIterator it3 = new IntArrayIterator(new int[][]{numbers}, numbers.length);
        IntArrayIterator smallest;
        
        for (int i = 0; i < totalSize; ++i) { 

            int[] result = new int[numbers.length]; //can be skipped?
            for (int j = 0; j < numbers.length; ++j) {
                smallest = it1.less(it2.less(it3));
                result[j] = smallest.get();
                smallest.move();
            }
            
            if (PCJ.myId() != 0)
                PCJ.put(parent, parentFieldName, result, i);
            else
                this.result[i] = result;
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
            /*for (int[] a : result) {
                System.out.println(Arrays.toString(a));
            }*/
        }
    }
}
