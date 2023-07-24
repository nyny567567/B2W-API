package com.finalProject.stockbeginner.trade.repository;

import com.finalProject.stockbeginner.trade.entity.Ranking;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class RankingRepositoryTest {

    @Autowired
    RankingRepository rankingRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("ranking add test")
    void addRank() {
        //given
        User user = userRepository.findByEmail("kcs123@naver.com").orElseThrow();
        Ranking ranking = Ranking.builder().profit(2500L).userName(user.getName()).email(user.getEmail()).build();
        //when
        Ranking save = rankingRepository.save(ranking);

        //then
    }

    @Test
    @DisplayName("get all rank")
    void allRanking() {
        //given
        //when
        List<Ranking> rankingList = rankingRepository.findAll(Sort.by(Sort.Direction.DESC, "profit"));
        //then
        int i=1;
        for(Ranking item : rankingList){
            if(item.getEmail().equals("kcs123@naver.com")){
                System.out.println("index:"+i);
                return;
            }
            i++;
        }
        System.out.println("kcs123@naver.com's rank: "+i);
    }
    
    @Test
    @DisplayName("change ranking point")
    void changePoint() {
        //given
        Ranking result = rankingRepository.findById("kcs123@naver.com").orElseThrow();

        Ranking ranking = Ranking.builder().profit(result.getProfit()-1200L).userName(result.getUserName()).email(result.getEmail()).build();
        //when
        Ranking save = rankingRepository.save(ranking);
        //then
    }
    
}