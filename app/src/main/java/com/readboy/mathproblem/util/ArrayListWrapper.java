package com.readboy.mathproblem.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/11/28.
 */

public class ArrayListWrapper<T> {

    private List<T> mValues = new ArrayList<T>();

    public T valuesAt(int index) {
        return mValues.get(index);
    }

    public int indexOfKey() {
        return -1;
    }

    public void put(int key, T values) {

    }

    public int remove(int key) {

        return -1;
    }

    public int removeAt(int index) {

        return -1;
    }

    public T get(int key) {

        return null;
    }

    public T valuseAt(int index) {

        return null;
    }

    public int indexOfKey(int key) {

        return -1;
    }

    public int indexOfValues(T values) {
        return -1;
    }

}
