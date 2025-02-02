package com.finalProject.stockbeginner.user.repository;

import com.finalProject.stockbeginner.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원가입 테스트")
    void saveTest() {
        User user = User.builder()
                .email("abc123@naver.com")
                .password("!1234qwer")
                .name("홍길동")
                .nick("홍천재")
                .build();

        User saved = userRepository.save(user);

        System.out.println(saved);
        assertNotNull(saved);
    }
}