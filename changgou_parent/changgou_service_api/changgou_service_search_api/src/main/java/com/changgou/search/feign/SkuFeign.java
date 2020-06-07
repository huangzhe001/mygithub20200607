package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/20 12:53
 */
@FeignClient(name = "search")
@RequestMapping("search")
public interface SkuFeign {
    /**搜索商品
     * @param searchMap
     * @return
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map<String, String> searchMap);
}
