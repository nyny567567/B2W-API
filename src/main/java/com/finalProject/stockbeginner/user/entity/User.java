package com.finalProject.stockbeginner.user.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@ToString @EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "stock_user")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "user_email", unique = true, nullable = false)
    private String email;

    @Column(name = "user_pw", nullable = false)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nick;

    @Builder.Default
    private Integer money = 5000000;

    @CreationTimestamp
    private LocalDateTime regDate;

    private String gender;

    private Integer age;

    private String career;

    private String image;

}
