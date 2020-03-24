package com.sr.pojo.vo.center;

import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * @author shirui
 * @date 2020/2/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MyOrdersVO {

    private String orderId;
    private Date createdTime;
    private Integer payMethod;
    private Integer realPayAmount;
    private Integer postAmount;
    private Integer isComment;
    private Integer orderStatus;
    private List<MySubOrderItemVO> subOrderItemList;
}
