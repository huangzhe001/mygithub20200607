package com.changgou.rabbitmq.listener.item;

import com.alibaba.fastjson.JSON;
import com.changgou.item.feign.PageFeign;
import entity.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/23 10:01
 */
@Component
public class HtmlGeneraListener {
    @Autowired
    private PageFeign pageFeign;

    /**生成静态页/删除静态页
     * @param msg
     */
    @RabbitListener
    public void getInfo(String msg){
        //将数据转换成Message
        Message message= JSON.parseObject(msg,Message.class);
        if(message.getCode()==2){
            //审核，生成静态页
            pageFeign.creatPageHtml(Long.parseLong(message.getContent().toString()));
        }
    }
}
