package com.finalProject.stockbeginner.user.service;

import com.finalProject.stockbeginner.user.dto.request.UserRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.response.UserRegisterResponseDTO;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserRegisterResponseDTO register(UserRegisterRequestDTO requestDTO) {
        log.info("service password, {}",requestDTO.getPassword());
        requestDTO.setPassword(encoder.encode(requestDTO.getPassword()));
        User saved = userRepository.save(requestDTO.toEntity());
        return new UserRegisterResponseDTO(saved);
    }
}
