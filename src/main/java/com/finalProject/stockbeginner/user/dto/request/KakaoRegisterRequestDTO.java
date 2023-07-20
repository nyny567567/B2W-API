package com.finalProject.stockbeginner.user.dto.request;

import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;

@Getter @Setter
@Builder
@EqualsAndHashCode(of = "email")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KakaoRegisterRequestDTO {

   private long kakaoId;
   private String password;
   private String nickname;
   private String image;
   private String email;
   private String gender;
   private String age;




   public User toEntity() {
      return User.builder()
              .kakaoId(getKakaoId())
              .email(getEmail())
              .password("kakao6987")
              .name("kakao")
              .nick(getNickname())
              .gender(getGender())
              .image(getImage())
              .build();
   }
}
