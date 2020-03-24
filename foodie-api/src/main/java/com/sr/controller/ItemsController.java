package com.sr.controller;

import com.sr.pojo.Items;
import com.sr.pojo.ItemsImg;
import com.sr.pojo.ItemsParam;
import com.sr.pojo.ItemsSpec;
import com.sr.pojo.vo.CommentLevelCountsVO;
import com.sr.pojo.vo.ItemInfoVO;
import com.sr.pojo.vo.ShopCartVO;
import com.sr.service.IItemService;
import com.sr.utils.PageGridResult;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author SR
 * @date 2019/11/19
 */
@Api(value = "商品接口", tags = {"商品信息展示的相关接口"})
@RestController
@RequestMapping("/items")
public class ItemsController {

    private IItemService iItemService;

    @Autowired
    public void setiItemService(IItemService iItemService) {
        this.iItemService = iItemService;
    }

    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public ServerResponse info(
            @ApiParam(name = "itemId", value = "商品Id", required = true)
            @PathVariable("itemId") String itemId){

        if (StringUtils.isBlank(itemId)){
            return ServerResponse.createByErrorMessage("商品Id为空");
        }

        Items items = iItemService.queryItemById(itemId);
        List<ItemsImg> itemsImgList = iItemService.queryItemImgList(itemId);
        List<ItemsSpec> itemsSpecList = iItemService.queryItemSpecList(itemId);
        ItemsParam itemsParam = iItemService.queryItemParam(itemId);

        ItemInfoVO itemInfoVo = ItemInfoVO.builder().item(items).itemImgList(itemsImgList).itemSpecList(itemsSpecList).itemParams(itemsParam).build();

        return ServerResponse.createBySuccess(itemInfoVo);
    }

    @ApiOperation(value = "查询商品等级", notes = "查询商品等级", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public ServerResponse commentLevel(
                                        @ApiParam(name = "itemId", value = "商品Id", required = true)
                                        @RequestParam("itemId") String itemId){
        if (StringUtils.isBlank(itemId)){
            return ServerResponse.createByErrorMessage("商品Id不能为空");
        }

        CommentLevelCountsVO commentLevelCountsVo = iItemService.queryCommentCounts(itemId);
        return ServerResponse.createBySuccess(commentLevelCountsVo);
    }

    @ApiOperation(value = "查询商品评论", notes = "查询商品评论", httpMethod = "GET")
    @GetMapping("/comments")
    public ServerResponse comments(
            @ApiParam(name = "itemId", value = "商品Id", required = true)
            @RequestParam("itemId") String itemId,
            @ApiParam(name = "level", value = "商品登记", required = false)
            @RequestParam("level") Integer level,
            @ApiParam(name = "page", value = "page", required = false)
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "pageSize", required = false)
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){

        if (StringUtils.isBlank(itemId)){
            return ServerResponse.createByErrorMessage("商品Id不能为空");
        }

        PageGridResult grid = iItemService.queryPageComments(itemId, level, page, pageSize);
        System.out.println("==");
        return ServerResponse.createBySuccess(grid);
    }

    @ApiOperation(value = "搜索商品列表", notes="搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public ServerResponse search(
            @ApiParam(name = "keywords", value = "关键字", required = true)
            @RequestParam("keywords") String keywords,
            @ApiParam(name = "排序", value = "排序", required = false)
            @RequestParam("sort") String sort,
            @ApiParam(name = "page", value = "page", required = false)
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "pageSize", required = false)
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){

        if (StringUtils.isBlank(keywords)){
            return ServerResponse.createByErrorMessage("关键字不能为空");
        }

        PageGridResult result = iItemService.searchItems(keywords, sort, page, pageSize);
        return ServerResponse.createBySuccess(result);
    }

    @ApiOperation(value = "通过分类id搜索商品列表", notes="通过分类id搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public ServerResponse catItems(
            @ApiParam(name = "catId", value = "三级分类id", required = true)
            @RequestParam("catId") Integer catId,
            @ApiParam(name = "排序", value = "排序", required = false)
            @RequestParam("sort") String sort,
            @ApiParam(name = "page", value = "page", required = false)
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "pageSize", required = false)
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){

        if (catId == null){
            return ServerResponse.createByErrorMessage("三级分类id不能为空");
        }

        PageGridResult result = iItemService.searchItems(catId, sort, page, pageSize);
        return ServerResponse.createBySuccess(result);
    }

    @ApiOperation(value = "根据商品规格ids查询最新的商品数据", notes = "根据商品规格ids查询最新的商品数据", httpMethod = "GET")
    @GetMapping("/refresh")
    public ServerResponse refresh(@ApiParam(name = "itemSpecIds", value = "拼接的规格Id", required = true, example = "1001,1002,1003,1004,1005") @RequestParam("itemSpecIds") String itemSpecIds){
        if (StringUtils.isBlank(itemSpecIds)){
            return ServerResponse.createByErrorMessage("id不能为空");
        }

        List<ShopCartVO> shopCartVoList = iItemService.queryItemsBySpecIds(itemSpecIds);

        return ServerResponse.createBySuccess(shopCartVoList);
    }
}
