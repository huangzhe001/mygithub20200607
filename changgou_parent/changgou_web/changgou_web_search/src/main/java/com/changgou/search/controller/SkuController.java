package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/20 13:00
 */
@Controller
@RequestMapping("search")
public class SkuController {
    @Autowired
    private SkuFeign skuFeign;

    /**
     * 搜索商品
     * 注意此处的@GetMapping()要添加list的url请求，不然会跟SkuFeign中的请求url冲突
     *
     * @param searchMap
     * @return
     */
    @GetMapping("list")
    public String search(@RequestParam(required = false) Map<String, String> searchMap, Model model) {
        //替换特殊字符
        handleSearchMap(searchMap);
        //查询数据
        Map result = skuFeign.search(searchMap);
        //查询结果返回给前台，用于渲染页面
        model.addAttribute("result", result);
        //将查询条件返回前台，用于回显
        model.addAttribute("searchMap", searchMap);
        //动态拼接url返回给前台
        String url = getUrl(searchMap);

        model.addAttribute("url", url);
        //将分页条件返回前台(使用分页工具类，传递三个参数，总记录数、当前页、每页显示数量)
        Page page = new Page(
                Long.parseLong(result.get("total").toString()),
                Integer.parseInt(result.get("pageNum").toString()) + 1,
                Integer.parseInt(result.get("pageSize").toString())
        );
        model.addAttribute("page", page);
        return "search";
    }

    /**
     * 特殊字符的处理
     *
     * @param searchMap
     */
    private void handleSearchMap(Map<String, String> searchMap) {
        if (searchMap != null) {
            Set<Map.Entry<String, String>> entrySet = searchMap.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                if (entry.getKey().startsWith("spec_")) {
                    entry.getValue().replace("+", "%2B");
                }
            }
        }
    }

    private String getUrl(@RequestParam(required = false) Map<String, String> searchMap) {
        String url = "/search/list";
        ///search/list?category=笔记本&brand=华为&spec_网络=移动4G&spec_颜色=红
        if (searchMap != null) {
            url += "?";
            for (String key : searchMap.keySet()) {
                //将排序的参数去掉（以sort开头的：sortRule,sortField）不用拼接到url上,
                // 如果是当前页不用将参数拼接到url上
                if (key.startsWith("sort") || "pageNum".equals(key)) {
                    continue;
                }
                url += key + "=" + searchMap.get(key) + "&";
            }
            //循环完后做后一个要去掉一个&
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
