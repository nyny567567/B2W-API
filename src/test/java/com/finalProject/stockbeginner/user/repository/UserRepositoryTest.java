package com.finalProject.stockbeginner.user.repository;

import com.finalProject.stockbeginner.user.dto.request.forceGradeDownRequestDTO;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.entity.UserRole;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("rank")
    void getRank() {
        //given

        //when
        List<User> result = userRepository.findAllByOrderByMoneyDesc();
        //then
        System.out.println("result = " + result);
    }

    @Test
    @DisplayName("강등")
    void forceGradeDown(forceGradeDownRequestDTO dto) {
        //given
        dto.getAdminEmail();
        //when
        Optional<User> black = userRepository.findByEmail(dto.getBlackEmail());
        //then
        if (black.isPresent()) {
            User user = black.get();
            user.setUserRole(UserRole.BLACK);
            System.out.println("user = " + user);

        }

    }

}
