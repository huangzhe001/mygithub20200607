package com.changgou.goods.feign;

import com.changgou.goods.pojo.Category;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/22 20:27
 */
@FeignClient(name = "goods")
@RequestMapping("category")
public interface CategoryFeign {
    /**根据id查询分类信息对象
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Category> findById(@PathVariable Integer id);
}
