package com.finalProject.stockbeginner.user.dto.request;

import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;
import org.apache.catalina.connector.Request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@Builder
@EqualsAndHashCode(of = "email")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRegisterRequestDTO {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 30)
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    private String userName;


    @NotBlank
    @Size(min = 2, max = 10)
    private String nick;

    private String gender;

    @Size(max = 120)
    private Integer age;

    private String career;

    private String image;

    private String mbti;
    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.userName)
                .nick(this.nick)
                .gender(this.gender)
                .age(this.age)
                .career(this.career)
                .image((this.image))
                .mbti(this.mbti)
                .build();
    }

}
