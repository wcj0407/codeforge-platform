package com.codeforge.codeforge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodeforgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeforgeApplication.class, args);
	}

}
