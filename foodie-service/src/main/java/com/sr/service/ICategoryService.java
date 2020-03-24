package com.sr.service;

import com.sr.pojo.Category;
import com.sr.pojo.vo.CategoryVO;
import com.sr.pojo.vo.NewItemsVO;

import java.util.List;

/**
 * @author SR
 * @date 2019/12/1
 */
public interface ICategoryService {

    /**
     * 查询所有一级分类
     * @return
     */
    List<Category> queryAllRootLevelCat(Integer type );

    /**
     * 根据一级分类ID查询子分类
     * @param rootCatId
     * @return
     */
    List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 查询首页每个一级分类下的6条最新商品数据
     * @param rootCatId
     * @return
     */
    List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);
}
