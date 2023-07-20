package com.finalProject.stockbeginner.trade.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RankResponseDTO {
    private Long rank;
    private String userName;
    private Long money;
}
