package com.lixiuxaing.seckill.exception;

import java.io.Serializable;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/18 16:26
 * @Version: 1.0
 */
public class RrException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -3817022850186181525L;
    private String msg;

    private int code = 500;

    public RrException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public RrException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public RrException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public RrException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}