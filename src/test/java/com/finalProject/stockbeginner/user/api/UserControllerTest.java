package com.finalProject.stockbeginner.user.api;

import com.finalProject.stockbeginner.user.dto.request.UserRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.response.UserRegisterResponseDTO;
import com.finalProject.stockbeginner.user.service.UserService;
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
class UserControllerTest {

    @Autowired
    private UserService userService;

//    @Test
//    @DisplayName("회원 가입")
//    void registerTest() {
//        //given
//        UserRegisterRequestDTO requestDTO = UserRegisterRequestDTO.builder()
//                .email("abc123@naver.com")
//                .name("김길동")
//                .nick("길동천재")
//                .password("!1234qwer")
//                .build();
//        //when
//        UserRegisterResponseDTO responseDTO = userService.register(requestDTO );
//        //then
//        assertEquals(responseDTO.getNick(),"길동천재");
//    }

}