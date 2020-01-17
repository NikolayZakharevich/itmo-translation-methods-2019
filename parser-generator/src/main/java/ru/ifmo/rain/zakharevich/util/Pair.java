package ru.ifmo.rain.zakharevich.util;


public class Pair<K, V> {

    public K getFirst() {
        return first;
    }

    public K first;

    public V getSecond() {
        return second;
    }

    public V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "F = " + first + ", S = " + second;
    }
}