package com.finalProject.stockbeginner.board.entity;

import com.finalProject.stockbeginner.board.dto.requestDTO.BoardRegisterRequestDTO;
import com.finalProject.stockbeginner.board.dto.requestDTO.BoardUpdateRequestDTO;
import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email")
    private User user;
    private String writer;
    @Column(nullable = false)
    private String title;
    private String content;
    @CreationTimestamp
    private LocalDateTime date;

    public Notice(BoardRegisterRequestDTO dto, User user){
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.writer = dto.getWriter();
        this.user = user;
    }

    public Notice(BoardUpdateRequestDTO dto, User user){
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.writer = dto.getWriter();
        this.user = user;
        this.id = dto.getId();
    }

}
