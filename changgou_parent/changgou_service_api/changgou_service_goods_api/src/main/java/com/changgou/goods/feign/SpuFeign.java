package com.changgou.goods.feign;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/18 17:17
 */
@FeignClient(name = "goods")//name属性代表服务的提供者名字
@RequestMapping("/spu")
public interface SpuFeign {
    /***
     * 根据ID查询Spu和sku数据
     * @param id
     * @return
     */
    @GetMapping("goods/{id}")
    public Result<Goods> findById(@PathVariable Long id);


}
