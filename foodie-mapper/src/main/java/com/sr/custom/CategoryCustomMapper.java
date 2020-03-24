package com.sr.custom;

import com.sr.pojo.vo.CategoryVO;
import com.sr.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CategoryCustomMapper{

    List<CategoryVO> getSubCatList(Integer rootCatId);

    List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap")Map<String, Object> maps);
}