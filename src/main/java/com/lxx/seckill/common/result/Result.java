package com.lxx.seckill.common.result;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面响应
 * @Author: lixiuxiang3
 * @Date: 2023/7/17 17:42
 * @Version: 1.0
 */
public class Result extends HashMap<String, Object> {

    private static final long serialVersionUID = -495405565955032270L;

    public Result() {
        put("code", 0);
    }

    public static Result error(int code, String msg) {
        Result r = new Result();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static Result error() {
        return error(500, "未知异常，请与管理员联系");
    }

    public static Result error(String msg) {
        return error(500, msg);
    }
    
    public static Result error(Object msg) {
        Result r = new Result();
        r.put("msg", msg);
        return r;
    }

    public static Result ok(Object msg) {
        Result r = new Result();
        r.put("msg", msg);
        return r;
    }

    public static Result ok(Map<String, Object> map) {
        Result r = new Result();
        r.putAll(map);
        return r;
    }

    public static Result ok() {
        return new Result();
    }

    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
