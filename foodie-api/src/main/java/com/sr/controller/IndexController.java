package com.sr.controller;

import com.sr.enums.CatsEnum;
import com.sr.enums.YesOrNoEnum;
import com.sr.pojo.Carousel;
import com.sr.pojo.Category;
import com.sr.pojo.vo.CategoryVO;
import com.sr.pojo.vo.NewItemsVO;
import com.sr.service.ICarouselService;
import com.sr.service.ICategoryService;
import com.sr.utils.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @Autowired
    public void setICarouselService(ICarouselService iCarouselService) {
        this.iCarouselService = iCarouselService;
    }

    @Autowired
    public void setICategoryService(ICategoryService iCategoryService) {
        this.iCategoryService = iCategoryService;
    }

    @ApiOperation(value = "获取首页轮播图列表", notes="获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public ServerResponse carousel(){
        List<Carousel> carouselList = iCarouselService.getCarousel(YesOrNoEnum.YES.getType());
        return ServerResponse.createBySuccess(carouselList);
    }

    @ApiOperation(value = "获取商品分类(一级分类)", notes="获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public ServerResponse cats(){
        List<Category> categoryList = iCategoryService.queryAllRootLevelCat(CatsEnum.ONE.getType());

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

        List<CategoryVO> subCatList = iCategoryService.getSubCatList(rootCatId);

        return ServerResponse.createBySuccess(subCatList);
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
