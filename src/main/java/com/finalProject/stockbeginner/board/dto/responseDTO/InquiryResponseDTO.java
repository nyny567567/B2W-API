package com.finalProject.stockbeginner.board.dto.responseDTO;

import com.finalProject.stockbeginner.board.entity.Inquiry;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InquiryResponseDTO {

    private String id;
    private String title;
    private String writer;
    private LocalDateTime date;

    public InquiryResponseDTO(Inquiry inquiry){
        this.id = inquiry.getId();
        this.title = inquiry.getTitle();;
        this.writer = inquiry.getWriter();
        this.date = inquiry.getDate();
    }
}
