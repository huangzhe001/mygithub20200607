package com.changgou.order.controller;

import com.changgou.goods.pojo.Sku;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/25 20:16
 */
@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {
    @Autowired
    private CartService cartService;

    @RequestMapping("/add")
    public Result add(Integer num, Long id) {
       // String username = "szitheima";
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        String username = userInfo.get("username");
        cartService.add(num, id, username);
        return new Result(true, StatusCode.OK,"商品添加到购物车成功");
    }

    /**查询用户的购物车数据列表
     * @param
     * @return
     */
    @GetMapping("/list")
    public Result list(){
       // String username="szitheima";
        String username = TokenDecode.getUserInfo().get("username");
        List<OrderItem> orderItems = cartService.list(username);
        return new Result(true,StatusCode.OK,"查询购物车数据列表成功",orderItems);
    }
}
