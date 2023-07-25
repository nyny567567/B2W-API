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

    private String id;
    private String title;
    private String writer;
    private LocalDateTime date;

    public NoticeResponseDTO(Notice notice){
        this.id = notice.getId();
        this.title = notice.getTitle();;
        this.writer = notice.getWriter();
        this.date = notice.getDate();
    }


}
