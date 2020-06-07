package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/18 17:46
 */
@RestController
@RequestMapping("search")
@CrossOrigin
public class SkuController {
    @Autowired
    private SkuService skuService;
    @GetMapping("import")
    public Result importSku(){
        skuService.importSku();
        return new Result(true, StatusCode.OK,"导入数据成功");
    }

    /**搜索商品
     * @param searchMap
     * @return
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map<String, String> searchMap){
        Map map = skuService.search(searchMap);
        return map;
    }
}
