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
   private KakaoRegisterRequestDTO kakaoRegisterRequestDTO;

   private int id;
   private final String userKakaoIdentifier = Integer.toString(kakaoRegisterRequestDTO.getId());
   private String nickname;
   private String image;
   private String email;
   private String gender;
   private String age;


   public KakaoRegisterRequestDTO(String email, String nickname, String userPassword, String nickname1, String id) {
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
