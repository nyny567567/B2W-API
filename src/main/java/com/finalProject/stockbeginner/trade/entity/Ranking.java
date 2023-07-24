package com.finalProject.stockbeginner.trade.entity;

import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "trade_ranking")
public class Ranking {


    @Id
    private String id;

    private User user;

    private Long profit;

}
