package com.finalProject.stockbeginner.user.auth;
import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Component
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TokenUserInfo {

    private String userId;
    private String email;
}
















