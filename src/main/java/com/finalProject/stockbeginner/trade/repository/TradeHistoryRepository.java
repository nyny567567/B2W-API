package com.finalProject.stockbeginner.trade.repository;

import com.finalProject.stockbeginner.trade.entity.TradeHistory;
import com.finalProject.stockbeginner.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeHistoryRepository extends JpaRepository<TradeHistory, String> {

    List<TradeHistory> findByUser(User user);

    //거래내역 최신순서로 보여주기 위한 코드
    List<TradeHistory> findByUserOrderByTradeDateDesc(User user);

}
