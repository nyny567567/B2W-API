package com.finalProject.stockbeginner.board.dto.requestDTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardRegisterRequestDTO {

    private String writer;
    private String title;
    private String content;
    private String email;
    private String type;



}
