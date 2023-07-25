package com.finalProject.stockbeginner.trade.repository;

import com.finalProject.stockbeginner.trade.entity.TradeHistory;
import com.finalProject.stockbeginner.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeHistoryRepository extends JpaRepository<TradeHistory, String> {

    List<TradeHistory> findByUser(User user);
    List<TradeHistory> findByUserOrderByTradeDateDesc(User user);
}
