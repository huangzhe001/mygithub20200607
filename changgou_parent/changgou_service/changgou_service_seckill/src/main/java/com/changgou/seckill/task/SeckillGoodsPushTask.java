package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/29 19:13
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class SeckillGoodsPushTask {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询秒杀商品存入redis
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void loadGoodsPushRedis() {
        //调用工具类获取时间段集合
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date startTime : dateMenus) {
            //根据时间段查询对应的秒杀商品数据
            //将时间段转换成字符串后面加上"SeckillGoods_"前缀组合成redis中的名称空间。便于查询
            String namespace = DateUtil.data2str(startTime, DateUtil.PATTERN_YYYYMMDDHH);
            //组装查询条件
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //商品必须审核通过
            criteria.andEqualTo("status", "1");
            //库存必须大于0
            criteria.andGreaterThan("stockCount", 0);
            //开始时间必须大于等于当前时间
            criteria.andGreaterThanOrEqualTo("startTime", startTime);
            //结束时间必须<当前时间+2小时
            criteria.andLessThan("endTime", DateUtil.addDateHour(startTime, 2));
            //排除之前已经加入到redis中的商品数据
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + namespace).keys();
            if (keys != null && keys.size() > 0) {
                criteria.andNotIn("id", keys);
            }
            //查询数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
            //将秒杀商品数据循环遍历缓存到redis中,id为key,秒杀商品对象为value
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps("SeckillGoods_" + namespace).put(seckillGood.getId(), seckillGood);
                //将商品库存数量缓存到redis,采用redis的自减（商品id为key,商品剩余库存数量为value）
                redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGood.getId(), seckillGood.getStockCount());
            }
        }
    }
}
