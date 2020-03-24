package com.sr.custom;

import com.sr.pojo.vo.ItemCommentVO;
import com.sr.pojo.vo.SearchItemsVO;
import com.sr.pojo.vo.ShopCartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author SR
 * @date 2019/12/26
 */
public interface ItemsCustomMapper {

    List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String, Object> map);

    List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String, Object> map);

    List<SearchItemsVO> searchItemsByThirdCat(@Param("paramsMap") Map<String, Object> map);

    List<ShopCartVO> queryItemsBySpecIds(@Param("specIdList") List<String> specIdList);

    int decreaseItemSpecStock(@Param("specId") String specId, @Param("pendingCounts") Integer pendingCounts);
}
