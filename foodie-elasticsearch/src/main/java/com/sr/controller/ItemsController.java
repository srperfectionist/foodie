package com.sr.controller;

import com.sr.pojo.Items;
import com.sr.service.IItemsESService;
import com.sr.utils.PageGridResult;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author shirui
 * @date 2020/2/17
 */
@RestController
@RequestMapping("/items/es")
@Slf4j
public class ItemsController {

    private IItemsESService iItemsESService;

    @Autowired
    public void setiItemsESService(IItemsESService iItemsESService) {
        this.iItemsESService = iItemsESService;
    }

    @ApiOperation(value = "搜索商品列表", notes="搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public ServerResponse search(
            @ApiParam(name = "keywords", value = "关键字", required = true)
            @RequestParam(name = "keywords") String keywords,
            @ApiParam(name = "排序", value = "排序", required = false)
            @RequestParam(name = "sort") String sort,
            @ApiParam(name = "page", value = "page", required = false)
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "pageSize", required = false)
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) throws IOException {

        if (StringUtils.isBlank(keywords)){
            return ServerResponse.createByErrorMessage("关键字不能为空");
        }

        page--;

        PageGridResult result = iItemsESService.searchItems(keywords, sort, page, pageSize);
        return ServerResponse.createBySuccess(result);
    }
}
