package com.readboy.mathproblem.cache;

/**
 * Created by oubin on 2017/9/22.
 */

public class ServerException extends Exception {

    private int errorNo;
    private String message;

    public ServerException(String msg) {
        this.message = msg;
    }

    public ServerException(int errorNo, String msg) {
        this.errorNo = errorNo;
        this.message = msg;
    }

    public int getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(int errorNo) {
        this.errorNo = errorNo;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ServerException: " + message;
    }
}
