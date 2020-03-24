package com.sr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author SR
 * @date 2019/11/22
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    /**
     * http://localhost:8088/swagger-ui.html     原路径
     * http://localhost:8088/doc.html     原路径
     * @return
     */
    @Bean
    public Docket createRestApi(){
                // 指定api类型为swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                // 用于定义api文档汇总信息
                    .apiInfo(apiINfo())
                    .select()
                // 指定controller包
                    .apis(RequestHandlerSelectors
                            .basePackage("com.sr.controller"))
                // 所有controller
                    .paths(PathSelectors.any())
                    .build();
    }

    private ApiInfo apiINfo(){
        return new ApiInfoBuilder()
                // 文档页标题
                .title("电商平台接口api")
                // 联系人信息
                .contact(new Contact("imooc",
                        "https://www.imooc.com",
                        "abc@imooc.com"))
                // 详细信息
                .description("电商平台提供的api文档")
                // 文档版本号
                .version("1.0.1")
                // 网站地址
                .termsOfServiceUrl("https://www.imooc.com")
                .build();
    }
}

