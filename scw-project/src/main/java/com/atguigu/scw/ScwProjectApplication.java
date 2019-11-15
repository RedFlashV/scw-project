package com.atguigu.scw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.atguigu.scw.project.utils.OSSTemplate;
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages="com.atguigu.scw.project.mapper")
public class ScwProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScwProjectApplication.class, args);
	}
	@ConfigurationProperties(prefix="oss")
	@Bean
	public OSSTemplate getOSSTemplate() {
		return new OSSTemplate();
	}
	
}
