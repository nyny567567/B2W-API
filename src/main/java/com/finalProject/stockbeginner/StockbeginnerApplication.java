package com.finalProject.stockbeginner;

import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.entity.UserRole;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class StockbeginnerApplication {
UserRepository userRepository;
	public static void main(String[] args) {
		SpringApplication.run(StockbeginnerApplication.class, args);
	}


	}


