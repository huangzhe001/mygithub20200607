package com.changgou.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**秒杀微服务的延时队列配置（处理超时未支付订单）
 * @author Alan
 * @version 1.0
 * @date 2019/12/3 15:43
 */
@Component
public class QueueConfig {
    @Value("${mq.pay.exchange.order}")
    private String seckillExchange;
    @Value("${mq.pay.queue.seckillordertimer}")
    private String seckillQueue;//有监听者的队列
    @Value("${mq.pay.queue.seckillordertimerdelay}")
    private String delaySeckillQueue;//死亡队列
    //创建死信队列
    @Bean
    public Queue delaySeckillOrderQueue(){
        return QueueBuilder.durable(delaySeckillQueue)
                .withArgument("x-dead-letter-exchange",seckillExchange)//指定交换机
                .withArgument("x-dead-letter-routing-key",seckillQueue)//指定路由到的队列名
                .build();
    }
    //创建真正的数据接收队列
    @Bean
    public Queue seckillOrderQueue(){
        return new Queue(seckillQueue,true);
    }
    //创建交换机
    @Bean
    public Exchange seckillExchange(){
        return new DirectExchange(seckillExchange);
    }
    //创建真正的数据接收队列绑定交换机
    @Bean
    public Binding seckillOrderQueueBindingExchange(Queue seckillOrderQueue,Exchange seckillExchange){
        return BindingBuilder.bind(seckillOrderQueue).to(seckillExchange)
                .with(seckillQueue).noargs();
    }
}
