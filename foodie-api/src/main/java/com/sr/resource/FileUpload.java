package com.sr.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author shirui
 * @date 2020/2/23
 */
@Component
@ConfigurationProperties(prefix = "foodie.fileupload")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileUpload {

    private String imageUserFaceLocation;
    private String imageServerUrl;
}
