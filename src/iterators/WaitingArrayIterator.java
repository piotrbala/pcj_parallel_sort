package iterators;

import org.pcj.PCJ;

/**
 * It uses PCJ to wait for data.
 *
 */
public class WaitingArrayIterator extends IntArrayIterator{

    /**
     * 
     * @param array to iterate over
     * @param size of 2nd dim arrays
     * @param fieldName name of array known by PCJ.
     */
    public WaitingArrayIterator(int[][] array, int size, String fieldName) {
        this.array = array;
        this.size = size;
        this.outer = -1;
        this.inner = size - 1;
        this.isEnd = false;
        this.fieldName = fieldName;
    }
    
    @Override
    public void move() {
        outer += (inner + 1) / size;
        inner = (inner + 1) % size;
        if (outer >= array.length) {
            isEnd = true;
            compare = a -> a;
            return;
        }
        while (array[outer] == null)
            PCJ.waitFor(fieldName);
    }

    @Override
    public int get() {
        return isEnd ? 0 : array[outer][inner];
    }

    @Override
    public boolean isEnd() {
        return isEnd;
    }

    private String fieldName;
    private boolean isEnd;
    private int[][] array;
    private int size, outer, inner;
    
}
