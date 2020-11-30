package com.sr.resource;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author shirui
 * @date 2020/11/27
 */
@Component
@ConfigurationProperties(prefix = "file")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FileResource {

    private String host;
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String objectName;
    private String ossHost;
}
