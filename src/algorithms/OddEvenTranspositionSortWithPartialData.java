package algorithms;

import iterators.extendedIterators.Direction;
import iterators.extendedIterators.Comparison;
import iterators.extendedIterators.ExtendedIntIterator;
import iterators.extendedIterators.GettingIterator;
import iterators.extendedIterators.NormalIterator;

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
  
    public final int SEND_SIZE = 64_000;
//    public final int SEND_SIZE = 1;
    @Shared
    private int[][] numbers;
  
  
    private int[][] tmp;
    private int myId, threadCount;

    /**
     * Sorts numbers.
     */
    private void localSort() {
        for (int[] a : numbers)
            Arrays.sort(a);
        
        int step = 1, temporary; //length of array merged
        while (step < numbers.length) {
            //first, second - currently merged parts
            int firstStart = 0, secondStart = step, tmpOuter = 0, tmpInner = 0;
            while (secondStart < numbers.length) {
                int firstOuter = firstStart, firstInner = 0;
                int secondOuter = secondStart, secondInner = 0;
                while (true) {
                   
                    if (firstOuter < secondStart && (
                            secondOuter >= numbers.length || secondOuter >= (secondStart + step)
                            || numbers[firstOuter][firstInner] < numbers[secondOuter][secondInner]
                            )) {
                        temporary = numbers[firstOuter][firstInner];
                        firstOuter += (firstInner + 1) / SEND_SIZE;
                        firstInner = (firstInner + 1) % SEND_SIZE;
                    }
                    else {
                        temporary = numbers[secondOuter][secondInner];
                        secondOuter += (secondInner + 1) / SEND_SIZE;
                        secondInner = (secondInner + 1) % SEND_SIZE;
                    }
                    tmp[tmpOuter][tmpInner] = temporary;
                    tmpOuter += (tmpInner + 1) / SEND_SIZE;
                    tmpInner = (tmpInner + 1) % SEND_SIZE;
                    
                    if ((secondOuter >= numbers.length || secondOuter >= (secondStart + step))
                            && firstOuter >= secondStart)
                            break;
                }
                
                firstStart = secondStart + step;
                secondStart = firstStart + step;
            }
            step *= 2;
            //swap
            int[][] s = tmp;
            tmp = numbers;
            numbers = s;
        }
        /*
        PCJ.log("Local sort done");
        String string = "";
        for (int[] a : numbers)
            string += Arrays.toString(a);
        PCJ.log(string);
        */
    }
    
    /**
     * Merges two arrays represented by iterators into result.
     */
    private void merge(ExtendedIntIterator it1, ExtendedIntIterator it2, ExtendedIntIterator result) {
        //Waiting for data and pointing first number
        it1.move();
        it2.move();
        result.move();
        ExtendedIntIterator c;
        while (!result.isEnd()) {
            c = it1.compare(it2);
            result.set(c.get());
            result.move();
            c.move();
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
  
        localSort();
  
        for (int phase = 1; phase < threadCount + 1; ++phase) {
            if (PhaseNeighbour[phase % 2] < threadCount && PhaseNeighbour[phase % 2] >= 0) {
                //ensure that neighbour finished previous calculations
                PCJ.barrier(PhaseNeighbour[phase % 2]);

                ExtendedIntIterator r, i1, i2;
                //we try to avoid sending too much data
                if (PhaseNeighbour[phase % 2] < myId){
                    r = new NormalIterator(Direction.DESCENDING, Comparison.GREATER, tmp, SEND_SIZE);
                    i1 = new NormalIterator(Direction.DESCENDING, Comparison.GREATER, numbers, SEND_SIZE);
                    i2 = new GettingIterator(Direction.DESCENDING, Comparison.GREATER, "numbers",
                            PhaseNeighbour[phase % 2], numbers.length, SEND_SIZE);
                }
                else {
                    r = new NormalIterator(Direction.ASCENDING, Comparison.LESSER, tmp, SEND_SIZE);
                    i1 = new NormalIterator(Direction.ASCENDING, Comparison.LESSER, numbers, SEND_SIZE);
                    i2 = new GettingIterator(Direction.ASCENDING, Comparison.LESSER, "numbers",
                            PhaseNeighbour[phase % 2], numbers.length, SEND_SIZE);
                }
                
                merge(i1, i2, r);
  
                //so we can securely cover previous data
                PCJ.barrier(PhaseNeighbour[phase % 2]);
  
                //swap
                int[][] s = tmp;
                tmp = numbers;
                numbers = s;
            }
        }
    }
  
    @Override
    public void main() throws Throwable {
        myId = PCJ.myId();
        threadCount = PCJ.threadCount();
      
        numbers = new int[Utils.SIZE/(threadCount * SEND_SIZE)][SEND_SIZE];
        tmp = new int[Utils.SIZE/(threadCount * SEND_SIZE)][SEND_SIZE];
      
      
        long t = 0, min = 0;
        for (int i = 0; i < 10; ++i) {
            for (int[] a : numbers)
                Utils.randomize(a);
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
        /*
        String string = "";
        for (int[] a : numbers)
            string += Arrays.toString(a);
        PCJ.log(string);
        */
    }
}
