package iterators.extendedIterators;

import java.util.function.Function;

public abstract class ExtendedIntIterator {
    
    public abstract ExtendedIntIterator compare(ExtendedIntIterator it);
    public abstract void move();
    public abstract int get();
    public abstract void set(int number);
    public abstract boolean isEnd();
    
    protected Function<ExtendedIntIterator, ExtendedIntIterator> lesser = (i1) -> {
        if (i1.isEnd())
            return this;
        return this.get() <= i1.get()? this : i1;
    };
    
    protected Function<ExtendedIntIterator, ExtendedIntIterator> greater = (i1) -> {
        if (i1.isEnd())
            return this;
        return this.get() > i1.get()? this : i1;
    };
}
