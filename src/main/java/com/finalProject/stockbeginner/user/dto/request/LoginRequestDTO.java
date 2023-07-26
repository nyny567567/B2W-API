package com.finalProject.stockbeginner.user.dto.request;

import com.finalProject.stockbeginner.user.entity.UserRole;
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

    private String image;

    private UserRole userRole;

    /** 아무 매개변수 없이 default값을 생성해주는 생성자 */
    public  LoginRequestDTO(){

        this.email = "1@naver.com";

        this.password = "admin1987##";

        this.image = null;

        this.userRole = UserRole.BRONZE;


    }


}
