package com.sr.controller;

import com.google.common.collect.Lists;
import com.sr.enums.CatsEnum;
import com.sr.enums.YesOrNoEnum;
import com.sr.pojo.Carousel;
import com.sr.pojo.Category;
import com.sr.pojo.vo.CategoryVO;
import com.sr.pojo.vo.NewItemsVO;
import com.sr.service.ICarouselService;
import com.sr.service.ICategoryService;
import com.sr.utils.JSONUtil;
import com.sr.utils.RedisOperator;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author SR
 * @date 2019/11/19
 */
@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("/index")
public class IndexController {

    private ICarouselService iCarouselService;

    private ICategoryService iCategoryService;

    private RedisOperator redisOperator;

    @Autowired
    public void setICarouselService(ICarouselService iCarouselService) {
        this.iCarouselService = iCarouselService;
    }

    @Autowired
    public void setICategoryService(ICategoryService iCategoryService) {
        this.iCategoryService = iCategoryService;
    }

    @Autowired
    public void setRedisOperator(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    @ApiOperation(value = "获取首页轮播图列表", notes="获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public ServerResponse carousel(){
        List<Carousel> carouselList = Lists.newArrayList();
        String carouselStr = redisOperator.get("carousel");
        if (StringUtils.isBlank(carouselStr)){
            carouselList = iCarouselService.getCarousel(YesOrNoEnum.YES.getType());
            redisOperator.set("carousel", JSONUtil.objToString(carouselList));
        } else{
            carouselList = JSONUtil.jsonToList(carouselStr, Carousel.class);
        }

        return ServerResponse.createBySuccess(carouselList);
    }

    /**
     * 1. 后台运营系统，一旦广告（轮播图）发生更改，就可以删除缓存，然后充值后
     * 2. 定时重置，比如每天凌晨三点重置
     * 3. 每个轮播图都有可能是一个广告，每个广告都会有一个过期时间，过期了，在重置
     */

    @ApiOperation(value = "获取商品分类(一级分类)", notes="获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public ServerResponse cats(){
        List<Category> categoryList = Lists.newArrayList();
        String catsStr = redisOperator.get("cats");

        if (StringUtils.isBlank(catsStr)){
            categoryList = iCategoryService.queryAllRootLevelCat(CatsEnum.ONE.getType());
            redisOperator.set("cats", JSONUtil.objToString(categoryList));
        } else{
            categoryList = JSONUtil.jsonToList(catsStr, Category.class);
        }

        return ServerResponse.createBySuccess(categoryList);
    }

    @ApiOperation(value = "获取商品子分类", notes="获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public ServerResponse subCat(
            @ApiParam(name="rootCatId", value = "一级分类Id", required = true)
            @PathVariable Integer rootCatId){
        if (rootCatId == null){
            return ServerResponse.createByErrorMessage("分类不存在");
        }

        List<CategoryVO> categoryVOList = Lists.newArrayList();
        String catsStr = redisOperator.get("subCat:" + rootCatId);

        if (StringUtils.isBlank(catsStr)){
            categoryVOList = iCategoryService.getSubCatList(rootCatId);

            /**
             * 查询的key在redis中不存在
             * 对应的id在数据库中也不存在
             * 此时被非法用户进行攻击，大量的请求会直接打在db上
             * 造成宕机，从而影响整个系统
             * 这种现象称之为缓存穿透
             * 解决方案：把空的数据也缓存起来，比如空的字符串、空的对象、空数组或list
             */

            if (CollectionUtils.isNotEmpty(categoryVOList)) {
                redisOperator.set("subCat:" + rootCatId, JSONUtil.objToString(categoryVOList));
            }else{
                redisOperator.set("subCat:" + rootCatId, JSONUtil.objToString(categoryVOList), 5*60);
            }
        } else{
            categoryVOList = JSONUtil.jsonToList(catsStr, CategoryVO.class);
        }

        return ServerResponse.createBySuccess(categoryVOList);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes="查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public ServerResponse sixNewItems(
            @ApiParam(name="rootCatId", value = "一级分类Id", required = true)
            @PathVariable Integer rootCatId){
        if (rootCatId == null){
            return ServerResponse.createByErrorMessage("分类不存在");
        }

        List<NewItemsVO> newItemsVos = iCategoryService.getSixNewItemsLazy(rootCatId);

        return ServerResponse.createBySuccess(newItemsVos);
    }
}
