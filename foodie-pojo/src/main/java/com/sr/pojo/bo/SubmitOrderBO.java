package com.sr.pojo.bo;

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
public class SubmitOrderBO {

    private String userId;
    private String itemSpecIds;
    private String addressId;
    private Integer payMethod;
    private String leftMsg;
}
