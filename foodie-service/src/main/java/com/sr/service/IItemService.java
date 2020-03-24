package com.sr.service;

import com.sr.pojo.Items;
import com.sr.pojo.ItemsImg;
import com.sr.pojo.ItemsParam;
import com.sr.pojo.ItemsSpec;
import com.sr.pojo.vo.CommentLevelCountsVO;
import com.sr.pojo.vo.ShopCartVO;
import com.sr.utils.PageGridResult;

import java.util.List;

/**
 * @author SR
 * @date 2019/12/17
 */
public interface IItemService {

    /**
     * 根据商品Id查询详情
     * @param itemId
     * @return
     */
    Items queryItemById(String itemId);

    /**
     * 根据商品Id查询商品图片列表
     * @param itemId
     * @return
     */
    List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品Id查询商品规格
     * @param itemId
     * @return
     */
    List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品Id查询商品参数
     * @param itemId
     * @return
     */
    ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品Id查询商品的评价等级数量
     * @param itemId
     * @return
     */
    CommentLevelCountsVO queryCommentCounts(String itemId);

    /**
     * 根据商品Id查询商品的评价
     * @param itemId
     * @param level
     * @param page
     * @param pageSize
     * @return
     */
    PageGridResult queryPageComments(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 搜索商品列表
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    PageGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

    /**
     * 根据分类id搜索商品列表
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    PageGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize);

    /**
     * 根据规格ids查询最新的购物车中的商品数据(用于刷新渲染购物车中的商品数据)
     * @param specIds
     * @return
     */
    List<ShopCartVO> queryItemsBySpecIds(String specIds);

    /**
     * 根据商品规格Id获取规格对象的具体信息
     * @param specId
     * @return
     */
    ItemsSpec queryItemSpecById(String specId);

    /**
     * 根据商品Id获取图片主图url
     * @param itemId
     * @return
     */
    String queryItemMainImgById(String itemId);

    /**
     * 减少库存
     * @param specId
     * @param buyCounts
     */
    void decreaseItemSpecStock(String specId, int buyCounts);
}
