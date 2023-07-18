package com.finalProject.stockbeginner.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FavoriteRequestDTO {

    @NotBlank
    private String stockCode;
    @NotBlank
    private String stockName;
    @NotBlank
    private String userEmail;


}
