package com.finalProject.stockbeginner.trade.entity;

import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "owned_stocks")
public class Stock {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @Column(name = "stock_id", nullable = false)
    private String stockId;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(nullable = false)
    private long price;
    @Column(nullable = false)
    private long quantity;

}
