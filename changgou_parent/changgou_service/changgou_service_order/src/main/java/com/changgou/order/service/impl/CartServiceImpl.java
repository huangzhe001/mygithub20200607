package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/25 19:48
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SkuFeign skuFeign;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SpuFeign spuFeign;

    /**添加商品到购物车
     * @param num:购买商品数量
     * @param skuId：购买商品的skuId
     * @param username：购买用户
     */
    @Override
    public void add(Integer num, Long skuId, String username) {
        //通过feign调用商品微服务，查询sku
        Sku sku = skuFeign.findById(skuId).getData();
        if (num < 1) {
            //如果购物车商品数量为0，要删除缓存中的购物车
            redisTemplate.boundHashOps("Cart_" + username).delete(skuId);
            return;
        }
        if (sku != null) {
            OrderItem orderItem = new OrderItem();
          //通过feign调用微服务查询spu
            Spu spu = spuFeign.findById(sku.getSpuId()).getData().getSpu();
            orderItem.setCategoryId1(spu.getCategory1Id());
            orderItem.setCategoryId2(spu.getCategory2Id());
            orderItem.setCategoryId3(spu.getCategory3Id());
            orderItem.setSkuId(skuId);
            orderItem.setSpuId(spu.getId());
            orderItem.setPrice(sku.getPrice());
            orderItem.setImage(sku.getImage());
            orderItem.setName(sku.getName());
            orderItem.setMoney(sku.getPrice()*num);
            orderItem.setWeight(sku.getWeight()*num);
            orderItem.setIsReturn("0");
            orderItem.setNum(num);
            orderItem.setPayMoney(orderItem.getMoney());
            //购物车数据缓存到redis中
            redisTemplate.boundHashOps("Cart_"+username).put(skuId,orderItem);
        }
    }

    @Override
    public List<OrderItem> list(String username) {
        List<OrderItem> orderItems = redisTemplate.boundHashOps("Cart_" + username).values();
        return orderItems;
    }
}
