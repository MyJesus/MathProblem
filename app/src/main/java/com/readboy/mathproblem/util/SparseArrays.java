package com.readboy.mathproblem.util;

import android.util.SparseArray;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by oubin on 2017/10/31.
 */

public class SparseArrays {

    public static <E> Set<E> valueSet(SparseArray<E> sparseArray){
        SparseArray<E> srcArray = sparseArray.clone();
        Set<E> values = new HashSet<>();
        int size = srcArray.size();
        for (int i = 0; i < size; i++) {
            values.add(sparseArray.valueAt(i));
        }
        return values;
    }

}
