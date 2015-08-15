package iterators.extendedIterators;

import java.util.function.Function;

public class NormalIterator extends ExtendedIntIterator {

    private int size, outer, inner;
    private int[][] array;
    private Function<ExtendedIntIterator, ExtendedIntIterator> compare;
    private boolean isEnd; //default false
    private Direction direction;
    
    public NormalIterator(Direction direction, Comparison comparison, int[][] array, int size) {
        this.array = array;
        this.size = size;
        
        switch (comparison) {
        case LESSER:
            compare = lesser;
            break;
        case GREATER:
            compare = greater;
            break;
        }
        
        this.direction = direction;
        switch (direction) {
        case ASCENDING:
            this.inner = size - 1;
            this.outer = -1;
            break;
        case DESCENDING:
            this.inner = 0;
            this.outer = array.length;
            break;
        }
        
    }
    
    @Override
    public ExtendedIntIterator compare(ExtendedIntIterator it) {
        return compare.apply(it);
    }

    @Override
    public void move() {
        if (isEnd)
            return;
        switch (direction) {
        case ASCENDING:
            outer += (inner + 1) / size;
            inner = (inner + 1) % size;
            isEnd = outer >= array.length;
            break;
        case DESCENDING:
            outer -= (size - inner) / size;
            inner = (inner - 1 + size) % size;
            isEnd = outer < 0;
            break;
        }
        if (isEnd) {
            compare = i -> i;
        }
    }

    @Override
    public int get() {
        if (isEnd)
            return 0;
        return array[outer][inner];
    }

    @Override
    public void set(int number) {
        if (isEnd)
            return;
        array[outer][inner] = number;
    }

    @Override
    public boolean isEnd() {
        return isEnd;
    }

}
