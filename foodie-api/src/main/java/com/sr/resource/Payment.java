package com.sr.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author shirui
 * @date 2020/2/23
 */
@Component
@ConfigurationProperties(prefix = "foodie.pay")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment {

    private String paymentUrl;
}
