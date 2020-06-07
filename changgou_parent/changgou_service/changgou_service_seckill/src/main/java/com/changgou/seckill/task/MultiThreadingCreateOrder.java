package com.changgou.seckill.task;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.IdWorker;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/30 14:50
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class MultiThreadingCreateOrder {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

    /**
     * 多线程抢单
     */
    @Async//此注解用于标识开启异步新线程
    public void creatOrder(Long count) {
        //从redis中依次取出排队信息 左进右出原则，保证顺序
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();
        if (seckillStatus != null) {
            //有排队信息
            String username = seckillStatus.getUsername();//用户信息
            String time = seckillStatus.getTime();//抢购商品对应的秒杀时间段
            Long id = seckillStatus.getGoodsId();//商品id
            //查询缓存中指定id的商品信息
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(id);
            //库存不足或者没有查到指定id的秒杀商品
            if (seckillGoods == null || count < 0) {
                throw new RuntimeException("商品已售罄");
            }
            //组装秒杀订单的内容
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());//主键自增id
            seckillOrder.setSeckillId(id);//秒杀商品的id
            seckillOrder.setCreateTime(new Date());//创建时间
            seckillOrder.setUserId(username);//用户
            seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
            seckillOrder.setStatus("0");//未支付
            //将秒杀订单存入redis中，SeckillOrder作为名称空间username作为key,秒杀订单信息作为value
            redisTemplate.boundHashOps("SeckillOrder").put(username, seckillOrder);
            //扣减库存（限制只能抢一件商品）
            seckillGoods.setStockCount(count.intValue());
            //扣减库存后秒杀商品信息重新放入redis
            redisTemplate.boundHashOps("SeckillGoods_" + time).put(id, seckillGoods);
            //判断当前秒杀商品是否有库存
            if (count <= 0) {
                //将商品数据同步到mysql中
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                //没有库存，需要清除redis中的数据
                //redisTemplate.boundHashOps("SeckillGoods_" + time).delete(id);
            }
            //抢单成功，更新抢单状态为待支付
            seckillStatus.setStatus(2);
            seckillStatus.setMoney(new Float(seckillGoods.getCostPrice()));//秒杀价格
            seckillStatus.setOrderId(seckillOrder.getId());//订单id
            //将抢单信息重新放入缓存，方便查询订单状态
            redisTemplate.boundHashOps("UserQueueStatus").put(username, seckillStatus);

            //发送延时消息到MQ中,超时未支付订单
           rabbitTemplate.convertAndSend(env.getProperty("mq.pay.queue.seckillordertimerdelay"), (Object) JSON.toJSONString(seckillStatus), new MessagePostProcessor() {
               @Override
               public Message postProcessMessage(Message message) throws AmqpException {
                   message.getMessageProperties().setExpiration("10000");//设置延时时间为10秒方便测试，一般设置半小时
                   return message;
               }
           });
        }
    }
}
