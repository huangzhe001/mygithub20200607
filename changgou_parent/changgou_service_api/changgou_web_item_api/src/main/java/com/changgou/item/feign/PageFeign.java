package com.changgou.item.feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/23 0:42
 */
@FeignClient(name = "item")
@RequestMapping("/page")
public interface PageFeign {
    /**根据spuId生成静态页面
     * @param spuId
     * @return
     */
    @RequestMapping ("/createHtml/{spuId}")
    public Result creatPageHtml(@PathVariable Long spuId);
}
