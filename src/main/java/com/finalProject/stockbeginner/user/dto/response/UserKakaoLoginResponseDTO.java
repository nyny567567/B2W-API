package com.finalProject.stockbeginner.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@Getter @Setter
@ToString
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserKakaoLoginResponseDTO {


    private String email;

    private String nick;

    private String access_Token; // 인증 토큰

    public UserKakaoLoginResponseDTO(User user, String access_Token){
        this.email = user.getEmail();
        this.access_Token = access_Token;
    }

    public UserKakaoLoginResponseDTO(HttpStatus httpStatus, String token, String email) {
    }
}
