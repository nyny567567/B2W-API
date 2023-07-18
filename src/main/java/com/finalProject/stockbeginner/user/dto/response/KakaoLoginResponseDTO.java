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

    private String token; // 인증 토큰

    private String image;

    public KakaoLoginResponseDTO(User user, String token){
        this.email = user.getEmail();
        this.nick = user.getNick();
        this.image = user.getImage();
        this.token = token;
    }

}
