package tools;

@FunctionalInterface
public interface Function<T, U, W, R> {
    public R apply(T t, U u, W w);
}
