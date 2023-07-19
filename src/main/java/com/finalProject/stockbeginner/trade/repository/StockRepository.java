package com.finalProject.stockbeginner.trade.repository;


import com.finalProject.stockbeginner.trade.entity.Stock;
import com.finalProject.stockbeginner.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, String> {
    List<Stock> getByUser(User user);
    Stock findOneByUserAndStockId(User user,String StockId);
}
