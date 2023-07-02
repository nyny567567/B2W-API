package com.finalProject.stockbeginner.user.api;

import com.finalProject.stockbeginner.user.dto.request.UserRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.response.UserRegisterResponseDTO;
import com.finalProject.stockbeginner.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    //이메일 중복 확인

    //회원 가입
    @PostMapping
    public ResponseEntity<?> register(@RequestBody UserRegisterRequestDTO requestDTO){
        log.info("controller password, {}",requestDTO.getPassword());
        try {
            UserRegisterResponseDTO responseDTO = userService.register(requestDTO);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //회원 수정

    //회원 탈퇴

    //

}
