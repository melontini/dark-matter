package me.melontini.dark_matter.api.base.util.classes;

import java.util.Objects;

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
