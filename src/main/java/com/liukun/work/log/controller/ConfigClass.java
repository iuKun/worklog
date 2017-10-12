package com.liukun.work.log.controller;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@ConfigurationProperties(prefix="customed.work")
@Configuration
@Data
public class ConfigClass {

	private String hour;
	private String hourPay;
	
}
