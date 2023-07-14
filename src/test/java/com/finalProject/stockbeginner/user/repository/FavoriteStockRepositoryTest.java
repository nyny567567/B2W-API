package com.finalProject.stockbeginner.user.repository;

import com.finalProject.stockbeginner.user.entity.FavoriteStock;
import com.finalProject.stockbeginner.user.entity.User;
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
class FavoriteStockRepositoryTest {

    @Autowired
    FavoriteStockRepository favoriteStockRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("favorite save test")
    void saveTest() {
        //given
        String dummyCode = "000660";
        String dummyStock = "하이닉스";
        User user = userRepository.findByEmail("abc123@naver.com").orElseThrow();

        FavoriteStock dummy = FavoriteStock.builder().stockCode(dummyCode).StockName(dummyStock).user(user).build();

        //when
        FavoriteStock saved = favoriteStockRepository.save(dummy);
        //then
        System.out.println("user:"+user);
        System.out.println("saved:"+saved);
    }

    @Test
    @DisplayName("중복 검사")
    void duplicateTest() {
        //given
        String dummyCode = "000660";
        User user = userRepository.findByEmail("abc123@naver.com").orElseThrow();
        //when
        Integer isExists = favoriteStockRepository.existsByUserAndStock(user, dummyCode);
        //then
        System.out.println("isExists = " + isExists);
    }

    @Test
    @DisplayName("삭제")
    void deleteTest() {
        //given
        String dummyCode = "000660";
        User user = userRepository.findByEmail("abc123@naver.com").orElseThrow();
        //when
        favoriteStockRepository.deleteById("4028808689528a2e0189528a4a1e0000");
        //then
    }

    @Test
    @DisplayName("f-id 찾기")
    void findTest() {
        //given
        String dummyCode = "000660";
        User user = userRepository.findByEmail("abc123@naver.com").orElseThrow();
        //when
        List<FavoriteStock> favoriteStock = favoriteStockRepository.findByUserAndStockCode(user, dummyCode);
        //then
        System.out.println("favoriteStock = " + favoriteStock);
    }

}