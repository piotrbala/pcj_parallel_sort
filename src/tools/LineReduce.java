package tools;

import java.io.Serializable;
import java.util.function.BiFunction;

import org.pcj.PCJ;

class LineReduce<A extends Serializable, V> extends Reduce<A, V>{

    protected LineReduce(BiFunction<A, V, A> function, A defaultAccumulator, String metaField) {
        this.function = function;
        this.accumulator = defaultAccumulator;
        this.metaField = metaField;
    }
    
    @Override
    public A calculate(V value) {
        A acc = accumulator;
        
        if (PCJ.myId() != PCJ.threadCount() - 1) {
            acc = PcjTools.waitForOuter(PCJ.myId() + 1, this.metaField, t -> t != null);
        }
        
        accumulator = function.apply(acc, value);
        PCJ.putLocal(this.metaField, accumulator);
        
        return accumulator;
    }

    
    private A accumulator;
    private String metaField;
    private BiFunction<A, V, A> function;
    
}
