package com.sr.service.center;

import com.sr.pojo.OrderItems;
import com.sr.pojo.bo.center.OrderItemsCommentBO;
import com.sr.utils.PageGridResult;

import java.util.List;

/**
 * @author shirui
 * @date 2020/2/23
 */
public interface IMyCommentsService {

    /**
     * 根据订单Id查询关联的商品
     * @param orderId
     * @return
     */
    List<OrderItems> queryPendingComment(String orderId);

    /**
     * 保存用户的评论
     * @param orderId
     * @param userId
     * @param commentBOList
     */
    void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentBOList);

    /**
     * 我的评价查询
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PageGridResult queryMyComments(String userId, Integer page, Integer pageSize);
}
