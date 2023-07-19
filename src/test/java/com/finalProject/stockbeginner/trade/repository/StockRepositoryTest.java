package com.finalProject.stockbeginner.trade.repository;

import com.finalProject.stockbeginner.trade.entity.Stock;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("추가 테스트")
    void addTest() {
        //given
        User user = userRepository.findByEmail("abc123@naver.com").orElseThrow();
        Stock stock = Stock.builder()
                .user(user).stockId("005930").stockName("삼성전자").price(144000).quantity(2)
                .build();
        //when
        Stock saved = stockRepository.save(stock);
        //then
        System.out.println("saved = " + saved);
    }
    @Test
    @DisplayName("유저가 보유한 전체 주식 조회")
    void getExistsStocks() {
        //given
        User user = userRepository.findByEmail("abc123@naver.com").orElseThrow();
        //when
        List<Stock> stocks = stockRepository.getByUser(user);
        //then
        System.out.println("stocks = " + stocks);
    }

    @Test
    @DisplayName("유저가 보유한 특정 주식 조회")
    void getOwnedStock() {
        //given
        User user = userRepository.findByEmail("abc123@naver.com").orElseThrow();
        //when
        Stock stock = stockRepository.findOneByUserAndStockId(user, "000666");
        //then
        System.out.println("stock = " + stock);
    }


    @Test
    @DisplayName("주식 판매")
    void sellStock() {
        //given
        User user = userRepository.findByEmail("abc123@naver.com").orElseThrow();
        Stock existingStock = stockRepository.findOneByUserAndStockId(user, "000660");
        //when
        Stock updateStock = Stock.builder()
                .user(user).stockId("000660").stockName("하이닉스").id(existingStock.getId())
                .quantity(existingStock.getQuantity() - 2).price(existingStock.getPrice() - 22000)
                .build();
        Stock saved = stockRepository.save(updateStock);
        //then
        System.out.println("saved = " + saved);
    }

    @Test
    @DisplayName("주식 추가구매")
    void moreAddStock() {
        //given
        User user = userRepository.findByEmail("abc123@naver.com").orElseThrow();
        Stock existingStock = stockRepository.findOneByUserAndStockId(user, "000660");
        //when
        Stock updateStock = Stock.builder().id(existingStock.getId())
                .user(user).stockId("000660").stockName("하이닉스")
                .quantity(existingStock.getQuantity() + 10).price(existingStock.getPrice() + 99000)
                .build();
        Stock saved = stockRepository.save(updateStock);
        //then
        System.out.println("saved = " + saved);
    }
}