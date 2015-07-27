import java.util.function.Function;


/**
 * 2nd dimension array has to have constant size.
 */
public class IntArrayIterator {

    boolean isEnd;
    private int[][] array;
    private int size, end, outer, inner;
    private Function<IntArrayIterator, IntArrayIterator> compare;
    
    
    private Function<IntArrayIterator, IntArrayIterator> normalCompare = a -> {
        if (a.isEnd())
            return this;
        else
            return a.get() <= this.get() ? a : this;
    };
    
    private Function<IntArrayIterator, IntArrayIterator> endCompare = a -> a;
    
    
    /**
     * 
     * @param array to index
     * @param size of 2nd dim array
     */
    public IntArrayIterator(int[][] array, int size) {
        this.array = array;
        this.size = size;
        this.end = array.length * size;
        this.outer = 0;
        this.inner = 0;
        if (0 == this.end) {
            this.compare = this.endCompare;
            this.isEnd = true;
        }
        else {
            this.compare = this.normalCompare;
            this.isEnd = false;
        }
    }
    
    /**
     * Compares this and that then returns less one.
     * @param it
     * @return 
     */
    public IntArrayIterator less(IntArrayIterator that) {
        return compare.apply(that);
    }
    
    public int get() {
        if (isEnd())
            return 0;
        else
            return array[outer][inner];
    }
    
    public void move() {
        if (isEnd)
            return;
        if (inner == size - 1) {
            inner = 0;
            ++outer;
            if (outer >= array.length) {
                isEnd = true;
                compare = endCompare;
            }
        }
        else {
            ++inner;
        }
        
        
    }
 
    public boolean isEnd() {
        return isEnd;
    }
    
    
}
