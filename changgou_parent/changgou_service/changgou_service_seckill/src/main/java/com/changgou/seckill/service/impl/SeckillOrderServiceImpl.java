package com.changgou.seckill.service.impl;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.task.MultiThreadingCreateOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****
 * @Author:sz.itheima
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;


    /**
     * SeckillOrder条件+分页查询
     *
     * @param seckillOrder 查询条件
     * @param page         页码
     * @param size         页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(seckillOrder);
        //执行搜索
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectByExample(example));
    }

    /**
     * SeckillOrder分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SeckillOrder> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectAll());
    }

    /**
     * SeckillOrder条件查询
     *
     * @param seckillOrder
     * @return
     */
    @Override
    public List<SeckillOrder> findList(SeckillOrder seckillOrder) {
        //构建查询条件
        Example example = createExample(seckillOrder);
        //根据构建的条件查询数据
        return seckillOrderMapper.selectByExample(example);
    }


    /**
     * SeckillOrder构建查询对象
     *
     * @param seckillOrder
     * @return
     */
    public Example createExample(SeckillOrder seckillOrder) {
        Example example = new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (seckillOrder != null) {
            // 主键
            if (!StringUtils.isEmpty(seckillOrder.getId())) {
                criteria.andEqualTo("id", seckillOrder.getId());
            }
            // 秒杀商品ID
            if (!StringUtils.isEmpty(seckillOrder.getSeckillId())) {
                criteria.andEqualTo("seckillId", seckillOrder.getSeckillId());
            }
            // 支付金额
            if (!StringUtils.isEmpty(seckillOrder.getMoney())) {
                criteria.andEqualTo("money", seckillOrder.getMoney());
            }
            // 用户
            if (!StringUtils.isEmpty(seckillOrder.getUserId())) {
                criteria.andEqualTo("userId", seckillOrder.getUserId());
            }
            // 创建时间
            if (!StringUtils.isEmpty(seckillOrder.getCreateTime())) {
                criteria.andEqualTo("createTime", seckillOrder.getCreateTime());
            }
            // 支付时间
            if (!StringUtils.isEmpty(seckillOrder.getPayTime())) {
                criteria.andEqualTo("payTime", seckillOrder.getPayTime());
            }
            // 状态，0未支付，1已支付
            if (!StringUtils.isEmpty(seckillOrder.getStatus())) {
                criteria.andEqualTo("status", seckillOrder.getStatus());
            }
            // 收货人地址
            if (!StringUtils.isEmpty(seckillOrder.getReceiverAddress())) {
                criteria.andEqualTo("receiverAddress", seckillOrder.getReceiverAddress());
            }
            // 收货人电话
            if (!StringUtils.isEmpty(seckillOrder.getReceiverMobile())) {
                criteria.andEqualTo("receiverMobile", seckillOrder.getReceiverMobile());
            }
            // 收货人
            if (!StringUtils.isEmpty(seckillOrder.getReceiver())) {
                criteria.andEqualTo("receiver", seckillOrder.getReceiver());
            }
            // 交易流水
            if (!StringUtils.isEmpty(seckillOrder.getTransactionId())) {
                criteria.andEqualTo("transactionId", seckillOrder.getTransactionId());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 增加SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void add(SeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }

    /**
     * 根据ID查询SeckillOrder
     *
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findById(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询SeckillOrder全部数据
     *
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }

    /**
     * 添加秒杀订单
     *
     * @param time
     * @param id
     * @param username
     * @return
     */
    @Override
    public boolean add(String time, Long id, String username) {
        //使用redis自增统计用户下单次数，防止重复下单,每次增加1，支付之后要重置为0(解决重复下单问题)
        Long recount = redisTemplate.boundHashOps("UserQueueCount").increment(username, 1);
        if(recount>1){
            throw new RuntimeException(StatusCode.REPEATERROR+"");//已有秒杀订单，不能重复下单
        }
        //防止超卖解决方案(库存自减)
        Long count = redisTemplate.boundHashOps("SeckillGoodsCount").increment(id, -1);
        if (count < 0) {
            throw new RuntimeException("商品已售罄！");
        }
        //排队信息的封装
        SeckillStatus seckillStatus = new SeckillStatus(username, new Date(), 1, id, time);//1表示状态为排队中
        //将排队信息放入redis对列中采用list分布式对列,左进右出原则
        redisTemplate.boundListOps("SeckillOrderQueue").leftPush(seckillStatus);
        //记录用户的排队信息到redis
        redisTemplate.boundHashOps("UserQueueStatus").put(username, seckillStatus);
        //使用多线程抢单(将库存数量带过去)
        multiThreadingCreateOrder.creatOrder(count);
        return true;
    }

    /**
     * 抢单状态查询
     *
     * @param username
     * @return
     */
    @Override
    public SeckillStatus queryStatus(String username) {

        return (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
    }

    /**
     * 修改订单状态
     *
     * @param out_trade_no
     * @param transaction_id
     * @param username
     */
    @Override
    public void updateStatus(String out_trade_no, String transaction_id, String username) {
        //从redis中查询订单数据
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
        if (seckillOrder != null) {
            seckillOrder.setStatus("1");//已支付
            seckillOrder.setPayTime(new Date());//支付时间
            seckillOrder.setTransactionId(transaction_id);//交易流水号
            //将订单信息同步到mysql
            seckillOrderMapper.insertSelective(seckillOrder);
            //清除redis中的排队信息
            redisTemplate.boundHashOps("UserQueueStatus").delete(username);
            //清除redis中秒杀订单信息
            redisTemplate.boundHashOps("SeckillOrder").delete(username);
            //将redis中的下单记录清除
            redisTemplate.boundHashOps("UserQueueCount").delete(username);
        }
    }

    /**
     * 支付失败，删除订单
     *
     * @param username
     */
    @Override
    public void closeOrder(String username) {
        //1.读取排队状态-redis.UserQueueStatus
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
        //2.获取Redis中订单信息-SeckillOrder
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
        //3.如果Redis中有订单信息，说明用户未支付
        if (seckillOrder != null) {
            //3.1从Redis中获取该商品-SeckillGoods_time.get(id)
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).get(seckillStatus.getGoodsId());
            if (seckillGoods == null) {
                //3.2如果Redis中没有，则从数据库中加载(根据商品id)
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
                //3.3库存回滚
                Long count = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillStatus.getGoodsId(), 1);
                //3.4更新库存
                seckillGoods.setStockCount(count.intValue());
                //3.5数据同步到Redis中-SeckillGoods_time,UserQueueCount,UserQueueStatus
                redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).put(seckillStatus.getGoodsId(), seckillGoods);
                //3.6清除redis中的信息
                //清除redis中的排队信息
                redisTemplate.boundHashOps("UserQueueStatus").delete(username);
                //清除redis中秒杀订单信息
                redisTemplate.boundHashOps("SeckillOrder").delete(username);
                //将redis中的下单记录清除
                redisTemplate.boundHashOps("UserQueueCount").delete(username);
            }
        }
    }
}
