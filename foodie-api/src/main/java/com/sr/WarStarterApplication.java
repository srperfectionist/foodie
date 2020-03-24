package com.sr;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 打包war [4] 增加war的启动类
 * @author shirui
 * @date 2020/3/7
 */
public class WarStarterApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 指向Application启动类
        return builder.sources(Application.class);
    }
}
