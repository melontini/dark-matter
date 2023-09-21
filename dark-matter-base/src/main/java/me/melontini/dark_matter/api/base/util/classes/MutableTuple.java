package me.melontini.dark_matter.api.base.util.classes;

import java.util.Objects;
import java.util.function.Function;

public final class MutableTuple<L, R> {

    public static <L, R> MutableTuple<L, R> of(L left, R right) {
        return new MutableTuple<>(left, right);
    }

    private L left;
    private R right;

    public MutableTuple(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    public void left(L left) {
        this.left = left;
    }

    public void right(R right) {
        this.right = right;
    }

    public boolean isLeftPresent() {
        return left != null;
    }

    public boolean isRightPresent() {
        return right != null;
    }

    public MutableTuple<R, L> swap() {
        return new MutableTuple<>(right, left);
    }

    public <V> MutableTuple<V, R> mapLeft(Function<? super L, ? extends V> mapper) {
        return new MutableTuple<>(mapper.apply(left), right);
    }

    public <V> MutableTuple<L, V> mapRight(Function<? super R, ? extends V> mapper) {
        return new MutableTuple<>(left, mapper.apply(right));
    }

    public <V, V1> MutableTuple<V, V1> mapBoth(Function<? super L, ? extends V> mapper, Function<? super R, ? extends V1> mapper1) {
        return new MutableTuple<>(mapper.apply(left), mapper1.apply(right));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableTuple<?, ?> that = (MutableTuple<?, ?>) o;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "MutableTuple{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
