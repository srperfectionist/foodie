package com.sr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author shirui
 * @date 2020/7/30
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.sr.mapper", "com.sr.custom"})
@ComponentScan(basePackages = {"com.sr", "org.n3r.idworker"})
public class FastDFSOSSApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastDFSOSSApplication.class, args);
    }
}
