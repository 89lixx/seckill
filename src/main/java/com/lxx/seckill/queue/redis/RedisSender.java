package com.lxx.seckill.queue.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/21 14:47
 * @Version: 1.0
 */

@Service
public class RedisSender {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 向通道发送消息的方法
    public void sendChannelMessage(String channel, String message) {
        stringRedisTemplate.convertAndSend(channel, message);
    }
}

