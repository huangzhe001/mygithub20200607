package com.changgou.goods.dao;
import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:sz.itheima
 * @Description:Brand的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface BrandMapper extends Mapper<Brand> {

    /**根据分类id查询对应的品牌列表
     * @param cid
     * @return
     */
    @Select("SELECT tb.* FROM tb_brand tb,tb_category_brand tcb " +
            "WHERE tcb.brand_id=tb.id AND tcb.category_id=#{cid}")
    List<Brand>findByCategoryId(Integer cid);
}
