package com.sr.service.impl;

import com.sr.pojo.Items;
import com.sr.service.IItemsESService;
import com.sr.util.ElasticsearchUtil;
import com.sr.utils.PageGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author shirui
 * @date 2020/11/23
 */
@Service("iItemsESService")
public class ItemsESServiceImpl implements IItemsESService {

    private ElasticsearchUtil elasticsearchUtil;

    @Autowired
    public void setElasticsearchUtil(ElasticsearchUtil elasticsearchUtil) {
        this.elasticsearchUtil = elasticsearchUtil;
    }

    @Override
    public PageGridResult searchItems(String keyword, String sort, Integer page, Integer pageSize) throws IOException {
        String sortName = "";
        String ascordesc = "";
        if(StringUtils.equals("c", sort)){
            sortName = "sellCounts";
            ascordesc = "desc";
        }else if(StringUtils.equals("p", sort)){
            sortName = "price";
            ascordesc = "asc";
        }else {
            sortName = "itemName.keyword";
            ascordesc = "asc";
        }
        return elasticsearchUtil.searchPageHighLight("foodie-items-ik","itemName", keyword, page, pageSize, sortName, ascordesc, Items.class);
    }
}
