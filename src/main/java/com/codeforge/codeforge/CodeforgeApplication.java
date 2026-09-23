package com.codeforge.codeforge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.codeforge.codeforge.user.mapper")
public class CodeforgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeforgeApplication.class, args);
	}

}
