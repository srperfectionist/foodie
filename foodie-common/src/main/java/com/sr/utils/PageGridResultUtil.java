package com.sr.utils;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author shirui
 * @date 2020/2/24
 */
public class PageGridResultUtil {

    public static PageGridResult setterPageGrid(List<?> list, Integer page){
        PageInfo<?> pageInfo = new PageInfo<>(list);
        PageGridResult pageGridResult = PageGridResult.builder().page(page)
                .rows(list)
                .total(pageInfo.getPages())
                .records(pageInfo.getTotal())
                .build();
        return pageGridResult;
    }
}
