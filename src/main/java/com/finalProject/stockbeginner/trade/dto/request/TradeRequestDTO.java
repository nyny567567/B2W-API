package com.finalProject.stockbeginner.trade.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TradeRequestDTO {

    private String email;
    private String stockName;
    private String stockId;
    private Long price;
    private Long quantity;



}
