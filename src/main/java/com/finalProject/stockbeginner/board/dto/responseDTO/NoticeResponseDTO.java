package com.finalProject.stockbeginner.board.dto.responseDTO;

import com.finalProject.stockbeginner.board.entity.Notice;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NoticeResponseDTO {

    private Long id;
    private String title;
    private String writer;
    private String content;
    private LocalDateTime date;
    private String email;

    public NoticeResponseDTO(Notice notice){
        this.id = notice.getId();
        this.title = notice.getTitle();;
        this.writer = notice.getWriter();
        this.date = notice.getDate();
        this.content = notice.getContent();
        this.email = notice.getUser().getEmail();
    }


}
