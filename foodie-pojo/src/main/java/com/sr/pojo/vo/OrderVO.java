package com.sr.pojo.vo;

import lombok.*;

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
}
