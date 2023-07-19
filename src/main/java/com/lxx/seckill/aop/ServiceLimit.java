package com.lxx.seckill.aop;


import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceLimit {
    String description() default "";

    String key() default "";

    LimitType limitType() default LimitType.CUSTOMER;
    enum LimitType {
        /**
         * 自定义key
         */
        CUSTOMER,
        /**
         * 数据请求的IP
         */
        IP
    }
}
