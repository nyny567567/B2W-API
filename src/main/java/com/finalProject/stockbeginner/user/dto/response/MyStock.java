package com.finalProject.stockbeginner.user.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "stockId")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MyStock {

    private String stockId;
    private String stockName;
    private Long price;
    private Long quantity;

}
