package com.finalProject.stockbeginner.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.entity.UserRole;
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

    private UserRole userRole; // 회원등급

    public LoginResponseDTO(User user, String token){
        this.email = user.getEmail();
        this.token = token;
        this.image = user.getImage();
        this.userRole = user.getUserRole();
    }


}
