package com.noah.syslog.util;

public class Pair<T, U> {

    public static <T, U> Pair of(T left, U right) {
        return new Pair<T, U>(left, right);
    }

    private T left;
    private U right;

    public Pair(T left, U right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public U getRight() {
        return right;
    }
}
