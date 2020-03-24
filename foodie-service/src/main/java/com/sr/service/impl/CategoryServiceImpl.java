package com.sr.service.impl;

import com.google.common.collect.Maps;
import com.sr.custom.CategoryCustomMapper;
import com.sr.mapper.CategoryMapper;
import com.sr.pojo.Category;
import com.sr.pojo.vo.CategoryVO;
import com.sr.pojo.vo.NewItemsVO;
import com.sr.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * @author SR
 * @date 2019/12/1
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private CategoryMapper categoryMapper;

    private CategoryCustomMapper categoryCustomMapper;

    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Autowired
    public void setCategoryCustomMapper(CategoryCustomMapper categoryCustomMapper) {
        this.categoryCustomMapper = categoryCustomMapper;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public List<Category> queryAllRootLevelCat(Integer type) {
        Example categoryExample = new Example(Category.class);
        Example.Criteria categoryCriteria = categoryExample.createCriteria();
        categoryCriteria.andEqualTo("type", type);

        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        return categoryList;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        return categoryCustomMapper.getSubCatList(rootCatId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId) {
        Map<String, Object> maps = Maps.newHashMap();
        maps.put("rootCatId", rootCatId);

        return categoryCustomMapper.getSixNewItemsLazy(maps);
    }
}
