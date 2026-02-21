package com.tobacco.warehouse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 延安烟厂三维数字孪生系统 后端启动类
 *
 * @author warehouse
 */
@SpringBootApplication
@MapperScan("com.tobacco.warehouse.**.mapper")
public class WarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  延安烟厂三维数字孪生系统 后端启动成功!");
        System.out.println("  API文档: http://localhost:8080/swagger-ui.html");
        System.out.println("=================================================");
    }
}
