package com.readboy.mathproblem.application;

/**
 * Created by oubin on 2017/9/22.
 */

public enum SubjectType {

    /**
     * 应用题指导
     */
    guide,
    /**
     * 应用题技巧
     */
    method;

//    public static SubjectType valueOf(String type) {
//        if ("guide".equalsIgnoreCase(type)) {
//            return SubjectType.guide;
//        } else if ("method".equalsIgnoreCase(type)) {
//            return SubjectType.method;
//        } else {
//            return null;
//        }
//    }

    public static SubjectType valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }



}
