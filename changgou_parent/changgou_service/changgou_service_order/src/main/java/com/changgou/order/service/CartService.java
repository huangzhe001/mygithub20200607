package com.changgou.order.service;


import com.changgou.order.pojo.OrderItem;

import java.util.List;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/25 19:47
 */
public interface CartService {
    /***
     * 添加购物车
     * @param num:购买商品数量
     * @param skuId：购买商品的skuId
     * @param username：购买用户
     * @return
     */
    void add(Integer num, Long skuId, String username);

    /**查询用户购物车数据列表
     * @param username
     * @return
     */
    List<OrderItem>list(String username);
}
