package me.melontini.dark_matter.api.base.util.classes;

import java.util.function.Function;

public record Tuple<L, R>(L left, R right) {

    public static <L, R> Tuple<L, R> of(L left, R right) {
        return new Tuple<>(left, right);
    }

    public boolean isLeftPresent() {
        return left != null;
    }

    public boolean isRightPresent() {
        return right != null;
    }

    public Tuple<R, L> swap() {
        return new Tuple<>(right, left);
    }

    public <V> Tuple<V, R> mapLeft(Function<? super L, ? extends V> mapper) {
        return new Tuple<>(mapper.apply(left), right);
    }

    public <V> Tuple<L, V> mapRight(Function<? super R, ? extends V> mapper) {
        return new Tuple<>(left, mapper.apply(right));
    }

    public <V, V1> Tuple<V, V1> mapBoth(Function<? super L, ? extends V> mapper, Function<? super R, ? extends V1> mapper1) {
        return new Tuple<>(mapper.apply(left), mapper1.apply(right));
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
