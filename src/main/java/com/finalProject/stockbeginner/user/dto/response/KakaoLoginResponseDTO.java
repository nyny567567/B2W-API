package com.finalProject.stockbeginner.user.dto.response;

import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;
import org.springframework.http.HttpStatus;


@Getter @Setter
@ToString
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoLoginResponseDTO {


    private String email;

    private String nick;

    private String access_Token; // 인증 토큰

    public KakaoLoginResponseDTO(User user, String access_Token){
        this.email = user.getEmail();
        this.access_Token = access_Token;
    }

    public KakaoLoginResponseDTO(HttpStatus httpStatus, String token, String email) {
    }
}
