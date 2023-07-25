package com.finalProject.stockbeginner.board.dto.requestDTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardDeleteRequestDTO {

    private String id;
    private String email;

}
