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

    private Long id;
    private String title;
    private String writer;
    private String content;
    private LocalDateTime date;
    private String email;

    public InquiryResponseDTO(Inquiry inquiry){
        this.id = inquiry.getId();
        this.title = inquiry.getTitle();;
        this.writer = inquiry.getWriter();
        this.date = inquiry.getDate();
        this.content = inquiry.getContent();
        this.email = inquiry.getUser().getEmail();
    }
}
