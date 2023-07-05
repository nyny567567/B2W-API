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
public class UserRegisterResponseDTO {

    private String email;

    private String name;

    private String nick;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime regDate;


    public UserRegisterResponseDTO(User user){
        this.email = user.getEmail();
        this.name = user.getName();
        this.nick = user.getNick();
        this.regDate = user.getRegDate();

    }
}
