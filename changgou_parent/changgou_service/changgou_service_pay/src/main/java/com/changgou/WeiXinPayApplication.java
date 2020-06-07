package com.changgou;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/28 15:47
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableEurekaClient
public class WeiXinPayApplication {

  @Autowired
  private Environment env;
    public static void main(String[] args) {
        SpringApplication.run(WeiXinPayApplication.class);
    }
    //声明交换机
    @Bean("orderExchange")
    public DirectExchange orderExchange(){
       return new DirectExchange(env.getProperty("mq.pay.exchange.order"),true,false);
    }
    //声明队列
    @Bean("orderQueue")
    public Queue orderQueue(){
        return new Queue(env.getProperty("mq.pay.queue.order"));
    }
    //声明队列绑定交换机
    @Bean
    public Binding orderBinding(@Qualifier("orderExchange") DirectExchange orderExchange,@Qualifier("orderQueue") Queue orderQueue ){
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(env.getProperty("mq.pay.routing.key"));
    }
    //声明秒杀队列
    @Bean("seckillQueue")
    public Queue seckillQueue(){
        return new Queue(env.getProperty("mq.pay.queue.seckillorder"));
    }
    //声明秒杀队列绑定交换机
    @Bean
    public Binding bindingSeckillOrder( DirectExchange orderExchange,Queue seckillQueue ){
        return BindingBuilder.bind(seckillQueue).to(orderExchange).with(env.getProperty("mq.pay.routing.seckillkey"));
    }
}
