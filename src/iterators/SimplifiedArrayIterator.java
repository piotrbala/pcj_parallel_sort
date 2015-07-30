package iterators;

/**
 * Iterator over single array. It has to contain all data.
 */
public class SimplifiedArrayIterator extends IntArrayIterator {

    public SimplifiedArrayIterator(int[] array) {
        this.array = array;
        this.index = -1;
        this.isEnd = false;
    }
    
    @Override
    public void move() {
        ++index;
        if (index == array.length) {
            isEnd = true;
            compare = a -> a;
        }
        
    }

    @Override
    public int get() {
        return isEnd ? 0 : array[index];
    }

    @Override
    public boolean isEnd() {
        return isEnd;
    }
    
    private boolean isEnd;
    private int[] array;
    private int index;

}
