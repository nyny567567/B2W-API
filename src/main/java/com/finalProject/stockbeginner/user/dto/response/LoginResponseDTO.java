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

    private String name;

    private String nick;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime regDate;

    private String token; // 인증 토큰

    public LoginResponseDTO(User user, String token){
        this.email = user.getEmail();
        this.name = user.getName();
        this.nick = user.getNick();
        this.regDate = user.getRegDate();
        this.token = token;
    }
}
