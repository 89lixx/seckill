package com.lxx.seckill.queue.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/21 17:32
 * @Version: 1.0
 */
@Component
public class KafkaSender {
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送消息到kafka
     */
    public void sendChannelMessage(String channel, String message) {
        kafkaTemplate.send(channel, message);
    }
}
