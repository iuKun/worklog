package com.liukun.work.log;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class WorkLogApplication {
	
	public static void main(String[] args) {
		ApplicationContext ac = SpringApplication.run(WorkLogApplication.class, args);
	}
}
