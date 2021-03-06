package algorithms;
import java.util.Arrays;
import java.util.function.BiFunction;

import org.pcj.PCJ;
import org.pcj.Shared;
import org.pcj.StartPoint;
import org.pcj.Storage;

import algorithms.common.Utils;
import tools.PcjTools;
import tools.Reduce;


public class NaiveMergeSort extends Storage implements StartPoint{
  
  @Shared
  private int[] meta;
  
  private int[] numbers;
  
  private void calculate() {
      
      Arrays.sort(numbers); //quick sort
      
      Reduce<int[],int[]> reduce = PcjTools.prepareTreeReduce(
              (a1, a2, a3) -> {
                  BiFunction<int[], int[], int[]> merge = (a, b) -> {
                      int[] result = new int[a.length + b.length];
                      int i = 0, j = 0;
                      while (i + j < result.length) {
                          if (i == a.length) {
                              result[i + j] = b[j++];
                          }
                          else if (j == b.length) {
                              result[i + j] = a[i++];
                          }
                          else {
                              result[i + j] = a[i] <= b[j] ? a[i++] : b[j++];
                          }
                      }
                      return result;
                  };
                  return merge.apply(a1, merge.apply(a2, a3));
              },
              new int[0],
              "meta"
          );
      int[] answer = reduce.calculate(numbers);
  }
  
  @Override
  public void main() throws Throwable {
      numbers = new int[Utils.SIZE/PCJ.threadCount()];

      long t = 0, min = 0;
      for (int i = 0; i < 10; ++i) {
          Utils.randomize(numbers);
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
