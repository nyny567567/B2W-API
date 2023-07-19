package com.finalProject.stockbeginner.trade.repository;

import com.finalProject.stockbeginner.trade.entity.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeHistoryRepository extends JpaRepository<TradeHistory, String> {
}
