package com.finalProject.stockbeginner.user.service;

import com.finalProject.stockbeginner.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Rollback(false)
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;


}