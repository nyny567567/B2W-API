package com.finalProject.stockbeginner.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchIdRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;
}
