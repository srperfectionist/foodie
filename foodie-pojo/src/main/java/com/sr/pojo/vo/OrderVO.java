package com.sr.pojo.vo;

import com.sr.pojo.bo.ShopCartBO;
import lombok.*;

import java.util.List;

/**
 * @author shirui
 * @date 2020/2/13
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVo;
    private List<ShopCartBO> toBeRemovedShopcatdList;
}
