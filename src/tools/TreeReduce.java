package tools;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.pcj.PCJ;


class TreeReduce<A extends Serializable, V> extends Reduce<A, V> {

    protected TreeReduce(Function<A, A, V, A> function, A defaultAccumulator, String metaField) {
        this.function = function;
        this.accumulator = defaultAccumulator;
        this.metaField = metaField;
    }
    
    @Override
    public A calculate(V value) {
        List<A> acc = new ArrayList<>();
        //default accumulator
        for (int i = 0; i < 2; ++i) {
            acc.add(this.accumulator);
        }
        
        int[] children;
        children = PcjTools.getChildren(PCJ.myId(), PCJ.threadCount());
        
        for (int i = 0; i < children.length; ++i) {
            acc.set(i, PcjTools.waitForOuter(children[i], this.metaField, t -> t != null));
        }
        accumulator = function.apply(acc.get(0), acc.get(1), value);
        PCJ.putLocal(this.metaField, accumulator);
        
        return accumulator;
    }
    
    private A accumulator;
    private String metaField;
    private Function<A, A, V, A> function;
}
