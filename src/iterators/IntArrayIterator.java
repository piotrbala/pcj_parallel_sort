package iterators;

import java.util.function.Function;

/**
 * Iterator over array. <strong>NOTE: </strong>In the beginning it points before array. 
 */
public abstract class IntArrayIterator {

    /**
     * Compares this and that then returns lesser one.
     */
    public IntArrayIterator lesser(IntArrayIterator that) {
        return this.compare.apply(that);
    }
    
    /**
     * Moves to next element. It may wait for data.
     */
    public abstract void move();
    
    /**
     * @return actual value pointed by this iterator
     */
    public abstract int get();
    
    /**
     * @return true iff there is no more data
     */
    public abstract boolean isEnd();
    
    protected Function<IntArrayIterator, IntArrayIterator> compare = a -> {
        if (a.isEnd())
            return this;
        else
            return a.get() <= this.get() ? a : this;
    };
}
