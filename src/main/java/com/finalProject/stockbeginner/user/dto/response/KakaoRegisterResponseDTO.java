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
public class KakaoRegisterResponseDTO {

    private String email;
    private String nick;
    private String gender;


    public KakaoRegisterResponseDTO(User user){
        this.email = user.getEmail();
        this.nick = user.getNick();
        this.gender = user.getGender();


    }
}
