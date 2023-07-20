package com.finalProject.stockbeginner.trade.repository;

import com.finalProject.stockbeginner.trade.entity.TradeHistory;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
class TradeHistoryTest {

    @Autowired
    private TradeHistoryRepository tradeHistoryRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("일단 추가")
    void addHistory() {
        //given
        User user = userRepository.findByEmail("abc123@naver.com").orElseThrow();
        TradeHistory history = TradeHistory.builder()
                .user(user).stockId("000660").stockName("하이닉스")
                .price(144000).quantity(2).tradeType("buy")
                .build();
        //when
        TradeHistory saved = tradeHistoryRepository.save(history);
        //then
        System.out.println("saved = " + saved);
    }

}
