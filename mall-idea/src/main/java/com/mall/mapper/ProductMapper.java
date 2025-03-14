package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * 商品Mapper接口
 */
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 查询轮播商品
     * @return
     */
    public List<Product> findSwiper();

    /**
     * 查询热卖商品
     * @return
     */
    public List<Product> findHot();

    /**
     * 根据条件 分页商品
     * @param map
     * @return
     */
    public List<Product> list(Map<String,Object> map);

    /**
     * 根据条件，查询商品总记录数
     * @param map
     * @return
     */
    public Long getTotal(Map<String,Object> map);

    /**
     * 添加商品
     * @param product
     * @return
     */
    public Integer add(Product product);

    /**
     * 修改商品
     * @param product
     * @return
     */
    public Integer update(Product product);


}
