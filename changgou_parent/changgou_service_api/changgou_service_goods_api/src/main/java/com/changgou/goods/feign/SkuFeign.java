package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/18 17:17
 */
@FeignClient(name = "goods")//name属性代表服务的提供者名字
@RequestMapping("/sku")
public interface SkuFeign {
    /**根据审核状态查询sku
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable String status);

    /***
     * 根据ID查询Sku数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable Long id);

    /**扣减库存
     * @param username
     * @return
     */
    @PostMapping("decr/count/{username}")
    public Result decrCount(@PathVariable String username);

    /**
     * 支付失败还原库存
     *
     * @param skuId
     */
    @GetMapping("update/stock/{num}/{skuId}")
    public Result updateSku(@PathVariable Integer num, @PathVariable Long skuId);

}
