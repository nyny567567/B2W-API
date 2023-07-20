package com.finalProject.stockbeginner.user.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteListResponseDTO {

    private String stockCode;

    private String stockName;


}
