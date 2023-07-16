package com.finalProject.stockbeginner.user.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString @EqualsAndHashCode
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    /** 아무 매개변수 없이 default값을 생성해주는 생성자 */
    public  LoginRequestDTO(){

        this.email = "1@naver.com";

        this.password = "admin1987##";


    }

}
