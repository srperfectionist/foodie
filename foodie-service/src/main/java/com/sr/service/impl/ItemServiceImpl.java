package com.sr.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.sr.custom.ItemsCustomMapper;
import com.sr.enums.CommentLevelEnum;
import com.sr.enums.YesOrNoEnum;
import com.sr.mapper.*;
import com.sr.pojo.*;
import com.sr.pojo.vo.CommentLevelCountsVO;
import com.sr.pojo.vo.ItemCommentVO;
import com.sr.pojo.vo.SearchItemsVO;
import com.sr.pojo.vo.ShopCartVO;
import com.sr.service.IItemService;
import com.sr.utils.DesensitizationUtil;
import com.sr.utils.PageGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * @author SR
 * @date 2019/12/17
 */
@Service("iItemService")
public class ItemServiceImpl implements IItemService {

    private ItemsMapper itemsMapper;
    private ItemsImgMapper itemsImgMapper;
    private ItemsSpecMapper itemsSpecMapper;
    private ItemsParamMapper itemsParamMapper;
    private ItemsCommentsMapper itemsCommentsMapper;
    private ItemsCustomMapper itemsCustomMapper;

    @Autowired
    public void setItemsMapper(ItemsMapper itemsMapper) {
        this.itemsMapper = itemsMapper;
    }

    @Autowired
    public void setItemsImgMapper(ItemsImgMapper itemsImgMapper) {
        this.itemsImgMapper = itemsImgMapper;
    }

    @Autowired
    public void setItemsSpecMapper(ItemsSpecMapper itemsSpecMapper) {
        this.itemsSpecMapper = itemsSpecMapper;
    }

    @Autowired
    public void setItemsParamMapper(ItemsParamMapper itemsParamMapper) {
        this.itemsParamMapper = itemsParamMapper;
    }

    @Autowired
    public void setItemsCommentsMapper(ItemsCommentsMapper itemsCommentsMapper) {
        this.itemsCommentsMapper = itemsCommentsMapper;
    }

    @Autowired
    public void setItemsCustomMapper(ItemsCustomMapper itemsCustomMapper) {
        this.itemsCustomMapper = itemsCustomMapper;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {
        Example itemsImgExample = new Example(ItemsImg.class);
        Example.Criteria itemsImgExampleCriteria = itemsImgExample.createCriteria();
        itemsImgExampleCriteria.andEqualTo("itemId", itemId);

        return itemsImgMapper.selectByExample(itemsImgExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example itemsSpecExample = new Example(ItemsSpec.class);
        Example.Criteria itemsSpecExampleCriteria = itemsSpecExample.createCriteria();
        itemsSpecExampleCriteria.andEqualTo("itemId", itemId);

        return itemsSpecMapper.selectByExample(itemsSpecExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public ItemsParam queryItemParam(String itemId) {
        Example itemsParamExample = new Example(ItemsParam.class);
        Example.Criteria itemsParamExampleCriteria = itemsParamExample.createCriteria();
        itemsParamExampleCriteria.andEqualTo("itemId", itemId);

        return itemsParamMapper.selectOneByExample(itemsParamExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {
        Integer goodCounts = getCommentCounts(itemId, CommentLevelEnum.GOOD.getType());
        Integer normalCounts = getCommentCounts(itemId, CommentLevelEnum.NORMAL.getType());
        Integer badCounts = getCommentCounts(itemId, CommentLevelEnum.BAD.getType());
        Integer totalCounts = goodCounts + normalCounts + badCounts;

        CommentLevelCountsVO commentLevelCountsVo = CommentLevelCountsVO.builder().goodCounts(goodCounts).normalCounts(normalCounts).badCounts(badCounts).totalCounts(totalCounts).build();
        return commentLevelCountsVo;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public PageGridResult queryPageComments(String itemId, Integer level, Integer page, Integer pageSize) {
        Map<String, Object> maps = Maps.newHashMap();
        maps.put("itemId", itemId);
        maps.put("level", level);

        PageHelper.startPage(page, pageSize);

        List<ItemCommentVO> itemCommentVoList = itemsCustomMapper.queryItemComments(maps);
        itemCommentVoList.forEach(ic -> ic.setNickname(DesensitizationUtil.commonDisplay(ic.getNickname())));

        PageInfo pageInfo = new PageInfo(itemCommentVoList);
        PageGridResult result = PageGridResult.builder().page(page).total(pageInfo.getPages()).records(pageInfo.getTotal()).rows(itemCommentVoList).build();
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public PageGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String, Object> maps = Maps.newHashMap();
        keywords = new StringBuilder().append("%").append(keywords).append("%").toString();
        maps.put("keywords", keywords);
        maps.put("sort", sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> searchItemsVoList = itemsCustomMapper.searchItems(maps);
        PageInfo pageInfo = new PageInfo(searchItemsVoList);
        PageGridResult result = PageGridResult.builder().page(page).total(pageInfo.getPages()).records(pageInfo.getTotal()).rows(searchItemsVoList).build();
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public PageGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> maps = Maps.newHashMap();
        maps.put("catId", catId);

        PageHelper.startPage(page, pageSize);

        if (StringUtils.equals("c", sort)){
            PageHelper.orderBy("i.sell_counts desc");
        }else if(StringUtils.equals("p", sort)){
            PageHelper.orderBy("tempSpec.price_discount asc");
        }else{
            PageHelper.orderBy("i.item_name asc");
        }

        List<SearchItemsVO> searchItemsVoList = itemsCustomMapper.searchItemsByThirdCat(maps);
        PageInfo pageInfo = new PageInfo(searchItemsVoList);
        PageGridResult result = PageGridResult.builder().page(page).total(pageInfo.getPages()).records(pageInfo.getTotal()).rows(searchItemsVoList).build();
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public List<ShopCartVO> queryItemsBySpecIds(String specIds) {
        List<String> specIdList = Splitter.on(",").splitToList(specIds);

        return itemsCustomMapper.queryItemsBySpecIds(specIdList);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public ItemsSpec queryItemSpecById(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNoEnum.YES.getType());
        ItemsImg result = itemsImgMapper.selectOne(itemsImg);
        return result != null ? result.getUrl() : "";
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void decreaseItemSpecStock(String specId, int buyCounts) {
        // synchronized：不推荐使用，集群下无用，性能低下
        // 锁数据库：不推荐使用，导致数据库性能低下
        // 分布式锁：zookeeper redis

        // 例如：lockUtil.getLock(); --加锁
        // lockUtil.unLock(); --解锁

        int result = itemsCustomMapper.decreaseItemSpecStock(specId, buyCounts);
        if (result != 1){
            throw new RuntimeException("订单创建失败，原因：库存不足！");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = {Exception.class, RuntimeException.class})
    Integer getCommentCounts(String itemId, Integer level){
        ItemsComments itemsComments = new ItemsComments();
        itemsComments.setItemId(itemId);
        if (level != null){
            itemsComments.setCommentLevel(level);
        }

        return itemsCommentsMapper.selectCount(itemsComments);
    }
}
