package com.readboy.mathproblem;

/**
 * Created by oubin on 2017/8/25.
 */

public class CloneTest implements Cloneable{

    public String mName;
    public CloneA cloneA;

    public CloneTest clone(){
        try {
            return (CloneTest) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
