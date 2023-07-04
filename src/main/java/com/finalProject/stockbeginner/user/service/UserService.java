package com.finalProject.stockbeginner.user.service;

import com.finalProject.stockbeginner.exception.DuplicatedEmailException;
import com.finalProject.stockbeginner.exception.NoRegisteredArgumentsException;
import com.finalProject.stockbeginner.user.auth.TokenProvider;
import com.finalProject.stockbeginner.user.auth.TokenUserInfo;
import com.finalProject.stockbeginner.user.dto.UserUpdateDTO;
import com.finalProject.stockbeginner.user.dto.request.LoginRequestDTO;
import com.finalProject.stockbeginner.user.dto.request.UserRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.response.LoginResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.UserRegisterResponseDTO;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;


    //회원 가입
    public UserRegisterResponseDTO register(UserRegisterRequestDTO requestDTO)

            throws RuntimeException {

        String email = requestDTO.getEmail();

        if(isDuplicate(email)) {
            throw new DuplicatedEmailException("중복된 이메일 입니다.");
        }

        log.info("service password, {}",requestDTO.getPassword());
        requestDTO.setPassword(encoder.encode(requestDTO.getPassword()));
        User saved = userRepository.save(requestDTO.toEntity());
        return new UserRegisterResponseDTO(saved);
    }

    //이메일 중복검사
    public boolean isDuplicate(String email) {

        return userRepository.existsByEmail(email);
    }

    // 회원 인증
    public LoginResponseDTO authenticate(LoginRequestDTO dto) {


        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("가입된 회원이 아닙니다!")
                );
        //패스워드 검증
        String rawPassword = dto.getPassword(); // 입력 비번
        String encodedPassword = user.getPassword(); // DB에 저장된 비번

        if(!encoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }


        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);
    }


    //회원정보수정

    @Transactional
    public LoginResponseDTO updateInfo(UserUpdateDTO dto, TokenUserInfo userInfo) {
        User user = userRepository
                .findById(userInfo.getUserId())
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));

        user.setPassword(dto.getPassword());
        user.setNick(dto.getNick());
        user.setImage(dto.getImage());

        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);

    }


    //회원 탈퇴
    @Transactional
        public void deleteUser(TokenUserInfo userInfo)
            throws NoRegisteredArgumentsException, IllegalStateException
    {
                if (userInfo.getUserId() == null) {
                throw new RuntimeException("로그인 유저 정보가 없습니다.");
            }
        userRepository.deleteById(userInfo.getUserId());
        }

}


