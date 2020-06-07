package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import com.changgou.order.service.OrderService;
import com.changgou.user.feign.UserFeign;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/****
 * @Author:sz.itheima
 * @Description:Order业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private UserFeign userFeign;


    /**
     * Order条件+分页查询
     *
     * @param order 查询条件
     * @param page  页码
     * @param size  页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Order> findPage(Order order, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(order);
        //执行搜索
        return new PageInfo<Order>(orderMapper.selectByExample(example));
    }

    /**
     * Order分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Order> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Order>(orderMapper.selectAll());
    }

    /**
     * Order条件查询
     *
     * @param order
     * @return
     */
    @Override
    public List<Order> findList(Order order) {
        //构建查询条件
        Example example = createExample(order);
        //根据构建的条件查询数据
        return orderMapper.selectByExample(example);
    }


    /**
     * Order构建查询对象
     *
     * @param order
     * @return
     */
    public Example createExample(Order order) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (order != null) {
            // 订单id
            if (!StringUtils.isEmpty(order.getId())) {
                criteria.andEqualTo("id", order.getId());
            }
            // 数量合计
            if (!StringUtils.isEmpty(order.getTotalNum())) {
                criteria.andEqualTo("totalNum", order.getTotalNum());
            }
            // 金额合计
            if (!StringUtils.isEmpty(order.getTotalMoney())) {
                criteria.andEqualTo("totalMoney", order.getTotalMoney());
            }
            // 优惠金额
            if (!StringUtils.isEmpty(order.getPreMoney())) {
                criteria.andEqualTo("preMoney", order.getPreMoney());
            }
            // 邮费
            if (!StringUtils.isEmpty(order.getPostFee())) {
                criteria.andEqualTo("postFee", order.getPostFee());
            }
            // 实付金额
            if (!StringUtils.isEmpty(order.getPayMoney())) {
                criteria.andEqualTo("payMoney", order.getPayMoney());
            }
            // 支付类型，1、在线支付、0 货到付款
            if (!StringUtils.isEmpty(order.getPayType())) {
                criteria.andEqualTo("payType", order.getPayType());
            }
            // 订单创建时间
            if (!StringUtils.isEmpty(order.getCreateTime())) {
                criteria.andEqualTo("createTime", order.getCreateTime());
            }
            // 订单更新时间
            if (!StringUtils.isEmpty(order.getUpdateTime())) {
                criteria.andEqualTo("updateTime", order.getUpdateTime());
            }
            // 付款时间
            if (!StringUtils.isEmpty(order.getPayTime())) {
                criteria.andEqualTo("payTime", order.getPayTime());
            }
            // 发货时间
            if (!StringUtils.isEmpty(order.getConsignTime())) {
                criteria.andEqualTo("consignTime", order.getConsignTime());
            }
            // 交易完成时间
            if (!StringUtils.isEmpty(order.getEndTime())) {
                criteria.andEqualTo("endTime", order.getEndTime());
            }
            // 交易关闭时间
            if (!StringUtils.isEmpty(order.getCloseTime())) {
                criteria.andEqualTo("closeTime", order.getCloseTime());
            }
            // 物流名称
            if (!StringUtils.isEmpty(order.getShippingName())) {
                criteria.andEqualTo("shippingName", order.getShippingName());
            }
            // 物流单号
            if (!StringUtils.isEmpty(order.getShippingCode())) {
                criteria.andEqualTo("shippingCode", order.getShippingCode());
            }
            // 用户名称
            if (!StringUtils.isEmpty(order.getUsername())) {
                criteria.andLike("username", "%" + order.getUsername() + "%");
            }
            // 买家留言
            if (!StringUtils.isEmpty(order.getBuyerMessage())) {
                criteria.andEqualTo("buyerMessage", order.getBuyerMessage());
            }
            // 是否评价
            if (!StringUtils.isEmpty(order.getBuyerRate())) {
                criteria.andEqualTo("buyerRate", order.getBuyerRate());
            }
            // 收货人
            if (!StringUtils.isEmpty(order.getReceiverContact())) {
                criteria.andEqualTo("receiverContact", order.getReceiverContact());
            }
            // 收货人手机
            if (!StringUtils.isEmpty(order.getReceiverMobile())) {
                criteria.andEqualTo("receiverMobile", order.getReceiverMobile());
            }
            // 收货人地址
            if (!StringUtils.isEmpty(order.getReceiverAddress())) {
                criteria.andEqualTo("receiverAddress", order.getReceiverAddress());
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (!StringUtils.isEmpty(order.getSourceType())) {
                criteria.andEqualTo("sourceType", order.getSourceType());
            }
            // 交易流水号
            if (!StringUtils.isEmpty(order.getTransactionId())) {
                criteria.andEqualTo("transactionId", order.getTransactionId());
            }
            // 订单状态,0:未完成,1:已完成，2：已退货
            if (!StringUtils.isEmpty(order.getOrderStatus())) {
                criteria.andEqualTo("orderStatus", order.getOrderStatus());
            }
            // 支付状态,0:未支付，1：已支付，2：支付失败
            if (!StringUtils.isEmpty(order.getPayStatus())) {
                criteria.andEqualTo("payStatus", order.getPayStatus());
            }
            // 发货状态,0:未发货，1：已发货，2：已收货
            if (!StringUtils.isEmpty(order.getConsignStatus())) {
                criteria.andEqualTo("consignStatus", order.getConsignStatus());
            }
            // 是否删除
            if (!StringUtils.isEmpty(order.getIsDelete())) {
                criteria.andEqualTo("isDelete", order.getIsDelete());
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
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Order
     *
     * @param order
     */
    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 增加Order
     *
     * @param order
     */
    @GlobalTransactional//开启分布式事务管理
    @Override
    public Order add(Order order) {
        //查询用户的购物车信息
        List<OrderItem> orderItems = cartService.list(order.getUsername());
        //完善订单信息
        order.setId("NO" + idWorker.nextId());//数据库主键不是自增，采用雪花算法获取主键id
        order.setCreateTime(new Date());//订单创建时间
        order.setUpdateTime(order.getCreateTime());//订单修改时间
        order.setUsername(order.getUsername());//用户名称
        order.setBuyerRate("0");//是否评价：0未评价
        order.setSourceType("1");//订单来源：web
        order.setOrderStatus("0");//未完成
        order.setPayStatus("0");//未支付
        order.setConsignStatus("0");//未发货
        order.setIsDelete("0");//未删除
        //根据购物车商品数据统计计算
        int totalNum = 0;//总数量
        int totalMoney = 0;//总金额
        //int totalPayMoney = 0;//实际付款
        //添加订单明细
        for (OrderItem orderItem : orderItems) {
            //订单详情表中没有自增主键，采用雪花算法，获取自增主键
            orderItem.setId("NO" + idWorker.nextId());
            orderItem.setOrderId(order.getId());//订单id
            orderItem.setIsReturn("0");//未退货
            //总金额
            totalMoney += orderItem.getMoney();
            //总数量
            totalNum += orderItem.getNum();
            //调用dao保存订单详情到数据库
            orderItemMapper.insertSelective(orderItem);
        }
        order.setTotalNum(totalNum);//总数量
        order.setTotalMoney(totalMoney);//总金额
        order.setPayMoney(totalMoney);//实付金额（总金额）
        //保存订单
        orderMapper.insert(order);
        //更改库存（调用商品微服务）

        int i=1/0;//(测试分布式事务制造异常)

        skuFeign.decrCount(order.getUsername());
        //增加积分（调用用户微服务）
        userFeign.addPoints(10);
        //下单后购物车数据要清除，数据都已保存到数据库
        //清除redis缓存中的购物车数据
        redisTemplate.delete("Cart_" + order.getUsername());
        //判断如果是在线支付，将订单信息缓存到redis中
        if (order.getSourceType().equals("1")) {
            redisTemplate.boundHashOps("orders").put(order.getId(), order);//订单id作为key
        }
        return order;
    }

    /**
     * 根据ID查询Order
     *
     * @param id
     * @return
     */
    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Order全部数据
     *
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 修改订单状态
     *
     * @param orderId        订单号
     * @param transaction_id 交易流水号
     */
    @Override
    public void updateStatus(String orderId, String transaction_id) {
        //从redis中查询订单信息
        Order order = (Order) redisTemplate.boundHashOps("orders").get(orderId);
        if (order != null) {
            order.setPayStatus("1");//已支付
            order.setOrderStatus("1");//已完成
            order.setUpdateTime(new Date());//修改时间
            order.setPayTime(order.getUpdateTime());//支付时间
            order.setTransactionId(transaction_id);//交易流水号
            orderMapper.updateByPrimaryKeySelective(order);
            //删除redis中的订单信息
            redisTemplate.boundHashOps("orders").delete(orderId);
        }
    }

    /**
     * 删除订单
     *
     * @param orderId
     */
    @Override
    public void deleteOrder(String orderId) {
        //从redis中获取订单信息
        Order order = (Order) redisTemplate.boundHashOps("orders").get(orderId);
        order.setUpdateTime(new Date());//修改时间
        order.setOrderStatus("2");//支付失败
        orderMapper.updateByPrimaryKeySelective(order);//修改订单
        //还原库存
        //根据订单id查询订单详情
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(orderId);
        //调用商品微服务还原库存
        skuFeign.updateSku(orderItem.getNum(), orderItem.getSkuId());
        //删除redis中的订单信息
        redisTemplate.boundHashOps("orders").delete(orderId);
    }
}
