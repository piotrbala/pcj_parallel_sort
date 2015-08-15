package iterators.extendedIterators;

import java.util.LinkedList;
import java.util.function.Function;

import org.pcj.FutureObject;
import org.pcj.PCJ;

public class GettingIterator extends ExtendedIntIterator {
    private final int BUF_SIZE = 2;

    private int size, outer, inner, downloaded, lastOrder, node; //downloaded is the number of last part that was got from field in node
    private String field;
    private int[][] array;
    private Function<ExtendedIntIterator, ExtendedIntIterator> compare;
    private boolean isEnd; //default false
    private Direction direction;
    LinkedList<FutureObject<int[]>> orders;
    
    public GettingIterator(Direction direction, Comparison comparison, String field, int node, int outerSize, int innerSize) {
        this.array = new int[outerSize][];
        this.size = innerSize;
        this.field = field;
        this.node = node;
        this.orders = new LinkedList<>();
        
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
            this.downloaded = -1;
            this.lastOrder = -1;
            break;
        case DESCENDING:
            this.inner = 0;
            this.outer = array.length;
            this.downloaded = array.length;
            this.lastOrder = array.length;
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
            if (!isEnd && outer > downloaded) {
                //taking care of new data
                for (int i = 0; i < (BUF_SIZE + 1 - orders.size()); ++i) {
                    if (++lastOrder >= array.length)
                        break;
                    orders.addLast(PCJ.getFutureObject(node, field, lastOrder));
                }
                array[outer] = orders.pollFirst().get();
                ++downloaded;
            }
            break;
        case DESCENDING:
            outer -= (size - inner) / size;
            inner = (inner - 1 + size) % size;
            isEnd = outer < 0;
            if (!isEnd && outer < downloaded) {
                //taking care of new data
                for (int i = 0; i < (BUF_SIZE + 1 - orders.size()); ++i) {
                    if (--lastOrder < 0)
                        break;
                    orders.addLast(PCJ.getFutureObject(node, field, lastOrder));
                }
                array[outer] = orders.pollFirst().get();
                if (array[outer] == null) {
                    PCJ.log("GOT NULL");
                }
                --downloaded;
            }
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
        return;
        //TODO is there a sense of this method?
    }

    @Override
    public boolean isEnd() {
        return isEnd;
    }

}
