package com.finalProject.stockbeginner.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;


@Getter @Setter
@ToString
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String email;

    private String image;

    private String token; // 인증 토큰

    public LoginResponseDTO(User user, String token){
        this.email = user.getEmail();
        this.token = token;
        this.image = user.getImage();
    }


}
