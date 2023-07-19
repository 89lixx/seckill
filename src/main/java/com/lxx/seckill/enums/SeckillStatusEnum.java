package com.lxx.seckill.enums;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/18 14:19
 * @Version: 1.0
 */
public enum SeckillStatusEnum {
    MUCH(2,"哎呦喂，人也太多了，请稍后！"),
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATE_REWRITE(-3,"数据篡改");
    private int state;
    private String info;

    SeckillStatusEnum(int state, String info) {
        this.state = state;
        this.info = info;
    }

    public int getState() {
        return state;
    }


    public String getInfo() {
        return info;
    }


    public static SeckillStatusEnum stateOf(int index) {
        for (SeckillStatusEnum state : values()) {
            if (state.getState()==index) {
                return state;
            }
        }
        return null;
    }
}
