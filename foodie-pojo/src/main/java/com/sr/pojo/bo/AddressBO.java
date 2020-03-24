package com.sr.pojo.bo;

import lombok.*;

/**
 * @author SR
 * @date 2020/1/13
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class AddressBO {

    private String addressId;
    private String userId;
    private String receiver;
    private String mobile;
    private String province;
    private String city;
    private String district;
    private String detail;
}
