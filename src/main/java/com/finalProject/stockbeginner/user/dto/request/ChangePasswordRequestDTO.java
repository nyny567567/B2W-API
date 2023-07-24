package com.finalProject.stockbeginner.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangePasswordRequestDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;
}
