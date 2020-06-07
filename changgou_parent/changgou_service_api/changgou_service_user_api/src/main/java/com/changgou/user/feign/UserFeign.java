package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/25 17:09
 */
@FeignClient(value ="user")//开启feign的客户端，value配置的是服务提供者的名称（yml文件里有）
@RequestMapping("user")
public interface UserFeign {
    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    @GetMapping("load/{id}")
    public Result<User> findById(@PathVariable String id);

    /**增加积分
     * @param point
     * @return
     */
    @GetMapping("/points/add")
    public Result addPoints(@RequestParam("point") Integer point);
}
