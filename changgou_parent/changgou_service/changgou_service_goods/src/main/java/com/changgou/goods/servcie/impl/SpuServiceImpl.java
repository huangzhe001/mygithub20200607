package com.changgou.goods.servcie.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.servcie.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:sz.itheima
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SkuMapper skuMapper;


    /**
     * Spu条件+分页查询
     *
     * @param spu  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     *
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu) {
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     *
     * @param spu
     * @return
     */
    public Example createExample(Spu spu) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (spu != null) {
            // 主键
            if (!StringUtils.isEmpty(spu.getId())) {
                criteria.andEqualTo("id", spu.getId());
            }
            // 货号
            if (!StringUtils.isEmpty(spu.getSn())) {
                criteria.andEqualTo("sn", spu.getSn());
            }
            // SPU名
            if (!StringUtils.isEmpty(spu.getName())) {
                criteria.andLike("name", "%" + spu.getName() + "%");
            }
            // 副标题
            if (!StringUtils.isEmpty(spu.getCaption())) {
                criteria.andEqualTo("caption", spu.getCaption());
            }
            // 品牌ID
            if (!StringUtils.isEmpty(spu.getBrandId())) {
                criteria.andEqualTo("brandId", spu.getBrandId());
            }
            // 一级分类
            if (!StringUtils.isEmpty(spu.getCategory1Id())) {
                criteria.andEqualTo("category1Id", spu.getCategory1Id());
            }
            // 二级分类
            if (!StringUtils.isEmpty(spu.getCategory2Id())) {
                criteria.andEqualTo("category2Id", spu.getCategory2Id());
            }
            // 三级分类
            if (!StringUtils.isEmpty(spu.getCategory3Id())) {
                criteria.andEqualTo("category3Id", spu.getCategory3Id());
            }
            // 模板ID
            if (!StringUtils.isEmpty(spu.getTemplateId())) {
                criteria.andEqualTo("templateId", spu.getTemplateId());
            }
            // 运费模板id
            if (!StringUtils.isEmpty(spu.getFreightId())) {
                criteria.andEqualTo("freightId", spu.getFreightId());
            }
            // 图片
            if (!StringUtils.isEmpty(spu.getImage())) {
                criteria.andEqualTo("image", spu.getImage());
            }
            // 图片列表
            if (!StringUtils.isEmpty(spu.getImages())) {
                criteria.andEqualTo("images", spu.getImages());
            }
            // 售后服务
            if (!StringUtils.isEmpty(spu.getSaleService())) {
                criteria.andEqualTo("saleService", spu.getSaleService());
            }
            // 介绍
            if (!StringUtils.isEmpty(spu.getIntroduction())) {
                criteria.andEqualTo("introduction", spu.getIntroduction());
            }
            // 规格列表
            if (!StringUtils.isEmpty(spu.getSpecItems())) {
                criteria.andEqualTo("specItems", spu.getSpecItems());
            }
            // 参数列表
            if (!StringUtils.isEmpty(spu.getParaItems())) {
                criteria.andEqualTo("paraItems", spu.getParaItems());
            }
            // 销量
            if (!StringUtils.isEmpty(spu.getSaleNum())) {
                criteria.andEqualTo("saleNum", spu.getSaleNum());
            }
            // 评论数
            if (!StringUtils.isEmpty(spu.getCommentNum())) {
                criteria.andEqualTo("commentNum", spu.getCommentNum());
            }
            // 是否上架,0已下架，1已上架
            if (!StringUtils.isEmpty(spu.getIsMarketable())) {
                criteria.andEqualTo("isMarketable", spu.getIsMarketable());
            }
            // 是否启用规格
            if (!StringUtils.isEmpty(spu.getIsEnableSpec())) {
                criteria.andEqualTo("isEnableSpec", spu.getIsEnableSpec());
            }
            // 是否删除,0:未删除，1：已删除
            if (!StringUtils.isEmpty(spu.getIsDelete())) {
                criteria.andEqualTo("isDelete", spu.getIsDelete());
            }
            // 审核状态，0：未审核，1：已审核，2：审核不通过
            if (!StringUtils.isEmpty(spu.getStatus())) {
                criteria.andEqualTo("status", spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 物理删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu.getIsDelete().equals("0")){
            throw new RuntimeException("此商品不能删除");
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    @Override
    public void saveGoods(Goods goods) {
        //保存spu
        Spu spu = goods.getSpu();

        //判断是否传入了spuId,如果没有传入，则执行新增操作，否则执行修改操作
        if (spu.getId() != null) {
            //执行修改操作，更新spu时删掉原有的sku列表
            spuMapper.updateByPrimaryKeySelective(spu);
            //删除原有的sku列表
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
        }
        //spu表中没有主键自增，调用idWorker自动生成
        spu.setId(idWorker.nextId());
        spuMapper.insertSelective(spu);
        //根据分类id查询分类信息
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        //根据品牌id查询品牌对象
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        //保存sku列表

        for (Sku sku : goods.getSkuList()) {
            if (StringUtils.isEmpty(sku.getSpec())) {
                sku.setSpec("{}");
            }
            //保存sku的分布式id
            sku.setId(idWorker.nextId());
            //获取sku的name(拼接规格的value)
            String name = spu.getName();
            //将规格(json串)转换成map
            Map<String, String> SpcMap = JSON.parseObject(sku.getSpec(), Map.class);
            for (String value : SpcMap.values()) {
                name += " " + value;
            }
            sku.setName(name);
            //保存图片
            sku.setImage(spu.getImage());
            //图片列表
            sku.setImages(spu.getImages());
            //创建时间
            sku.setCreateTime(new Date());
            //更新时间
            sku.setUpdateTime(new Date());
            //spuId
            sku.setSpuId(spu.getId());
            //商品分类id
            sku.setCategoryId(category.getId());
            //商品分类名称
            sku.setCategoryName(category.getName());
            //品牌名称
            sku.setBrandName(brand.getName());
            //调用dao执行保存操作
            skuMapper.insertSelective(sku);
        }
    }

    /**
     * 根据spuid查询商品信息的集合
     *
     * @param spuId
     * @return
     */
    @Override
    public Goods findGoodsById(Long spuId) {
        //创建商品对象
        Goods goods = new Goods();
        //根据spuId查询spu信息
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        goods.setSpu(spu);
        //根据spuId查询sku列表信息
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        goods.setSkuList(skuList);
        return goods;
    }

    /**审核
     * @param spuId
     */
    @Override
    public void audit(Long spuId) {
        //根据spuId查询spu对象
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(spu.getIsDelete().equals("1")){
            throw new RuntimeException("此商品已经被删除，不能审核");
        }
        spu.setStatus("1");//设置为已审核
        spu.setIsMarketable("1");//设置为已上架
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**商品下架
     * @param spuId
     */
    @Override
    public void pull(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(spu.getIsDelete().equals("1")){
            throw new RuntimeException("此商品已经下架，无法删除");
        }
        //spu.setStatus("0");数据库默认的状态
        spu.setIsMarketable("0");//已下架
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**商品上架
     * @param spuId
     */
    @Override
    public void put(Long spuId) {
        //根据spuId查询到具体的商品对象
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(spu.getIsDelete().equals("1")){
            throw new RuntimeException("此商品已经被删除，不能上架");
        }
        if(spu.getStatus().equals("0")){
            throw new RuntimeException("商品未审核，不能上架");
        }
        spu.setStatus("1");
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**批量上架
     * @param ids
     * @return
     */
    @Override
    public int putMany(Long... ids) {
        Spu spu=new Spu();
        //修改状态为上架
        spu.setIsMarketable("1");
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));//数组转换成集合Arrays.asList()
        int i = spuMapper.updateByExampleSelective(spu, example);
        return i;
    }

    /**逻辑删除
     * @param spuId
     */
    @Override
    public void logicDelete(Long spuId) {
        //根据spuId查询spu对象
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(!spu.getIsMarketable().equals("0")){
            throw new RuntimeException("未下架的商品不能删除");
        }
        spu.setIsDelete("1");//设置为已删除
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**还原被删除的商品
     * @param spuId
     */
    @Override
    public void restore(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(spu.getIsDelete().equals("0")){
            throw new RuntimeException("未被逻辑删除，不能还原");
        }
        spu.setIsDelete("0");//还原数据状态
        //执行修改操作
        spuMapper.updateByPrimaryKeySelective(spu);

    }
}
