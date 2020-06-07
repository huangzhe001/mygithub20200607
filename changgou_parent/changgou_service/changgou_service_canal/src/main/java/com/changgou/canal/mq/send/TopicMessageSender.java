package com.changgou.canal.mq.send;

import com.alibaba.fastjson.JSON;
import entity.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/23 9:45
 */
@Component
public class TopicMessageSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**topic发送消息
     * @param message
     */
    public void sendMessage(Message message) {
        rabbitTemplate.convertAndSend(message.getExechange(),
                message.getRoutekey(),
                JSON.toJSONString(message));
    }
}
