package com.my.picturesystembackend;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class}) // 排查分库分表注入
@MapperScan("com.my.picturesystembackend.mapper")
@EnableAsync
@EnableAspectJAutoProxy(exposeProxy = true) // EnableAspectJAutoProxy注解用于开启对AspectJ的支持，exposeProxy = true表示暴露代理对象，以便在同一个类中调用被AOP增强的方法时，仍然能够触发AOP逻辑。
public class PictureSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureSystemBackendApplication.class, args);
    }

}
