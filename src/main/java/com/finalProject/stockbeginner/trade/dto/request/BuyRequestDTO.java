package com.finalProject.stockbeginner.trade.dto.request;

import com.finalProject.stockbeginner.trade.entity.Stock;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BuyRequestDTO {

    private String email;
    private String stockName;
    private String stockId;
    private Long price;
    private Long quantity;



}
