package com.unacademy.redis;

import com.unacademy.redis.service.MainService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RedisApplication.class, args);

		MainService msobj = new MainService();
		msobj.preprocess();

	}

}
