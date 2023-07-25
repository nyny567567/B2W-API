package com.finalProject.stockbeginner.trade.entity;

import com.finalProject.stockbeginner.trade.dto.request.TradeRequestDTO;
import com.finalProject.stockbeginner.user.entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "trade_history")
public class TradeHistory {

    @Id
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
    @CreationTimestamp
    private LocalDateTime tradeDate;
    @Column(nullable = false)
    private String tradeType;

    public TradeHistory(TradeRequestDTO dto, User user,String type){
        this.stockId = dto.getStockId();
        this.stockName = dto.getStockName();
        this.price = dto.getPrice();
        this.quantity = dto.getQuantity();
        this.user = user;
        this.tradeType = type;
    }
}
