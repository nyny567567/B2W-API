package com.finalProject.stockbeginner.user.dto.request;

import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@Builder
@EqualsAndHashCode(of = "email")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserKakaoSignupRequestDTO {
   private UserKakaoSignupRequestDTO userKakaoSignupRequestDTO;

   private int id;
   private final String userKakaoIdentifier = Integer.toString(userKakaoSignupRequestDTO.getId());
   private String nickname;
   private String image;
   private String email;
   private String gender;
   private String age;


   public UserKakaoSignupRequestDTO(String email, String nickname, String userPassword, String nickname1, String id) {
   }

   public User toEntity() {
      return User.builder()
              .email(getEmail())
              .password(String.valueOf(-1))
              .name(userKakaoIdentifier)
              .nick(getNickname())
              .gender(getGender())
              .image(getImage())
              .build();
   }
}
