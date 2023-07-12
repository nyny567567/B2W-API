package com.finalProject.stockbeginner.user.dto.response;

import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private String email;

    public UserResponseDTO(User user) {
    }
}
