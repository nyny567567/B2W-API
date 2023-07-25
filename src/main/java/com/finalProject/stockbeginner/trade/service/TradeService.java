package com.finalProject.stockbeginner.trade.service;

import com.finalProject.stockbeginner.trade.dto.request.TradeRequestDTO;
import com.finalProject.stockbeginner.trade.dto.response.RankResponseDTO;
import com.finalProject.stockbeginner.trade.entity.Ranking;
import com.finalProject.stockbeginner.trade.entity.Stock;
import com.finalProject.stockbeginner.trade.entity.TradeHistory;
import com.finalProject.stockbeginner.trade.repository.RankingRepository;
import com.finalProject.stockbeginner.trade.repository.StockRepository;
import com.finalProject.stockbeginner.trade.repository.TradeHistoryRepository;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final TradeHistoryRepository tradeHistoryRepository;
    private final StockRepository stockRepository;
    private final RankingRepository rankingRepository;

    public String buyStock(TradeRequestDTO requestDTO){
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow();
        Stock existingStock = stockRepository.findOneByUserAndStockId(user, requestDTO.getStockId());
        Stock newStock;
        try {
            if(existingStock == null){
                newStock = Stock.builder()
                        .user(user).stockId(requestDTO.getStockId()).stockName(requestDTO.getStockName())
                        .price(requestDTO.getPrice()).quantity(requestDTO.getQuantity())
                        .build();

            }else {
                newStock = Stock.builder()
                        .user(user).id(existingStock.getId())
                        .stockId(requestDTO.getStockId()).stockName(requestDTO.getStockName())
                        .price(existingStock.getPrice()+requestDTO.getPrice())
                        .quantity(existingStock.getQuantity()+requestDTO.getQuantity())
                        .build();
            }
            Stock savedStock = stockRepository.save(newStock);
            user.setMoney(user.getMoney()- requestDTO.getPrice());
            User savedUser = userRepository.save(user);
            TradeHistory history = TradeHistory.builder()
                    .user(user).stockId(requestDTO.getStockId()).stockName(requestDTO.getStockName())
                    .price(requestDTO.getPrice()).quantity(requestDTO.getQuantity()).tradeType("buy")
                    .build();
            TradeHistory savedHistory = tradeHistoryRepository.save(history);
            return "success";
        } catch (Exception e) {
            return "fail";
        }

    }

    public String sellStock(TradeRequestDTO requestDTO){
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow();
        Stock existingStock = stockRepository.findOneByUserAndStockId(user, requestDTO.getStockId());

        Stock newStock;
        try {
            if(existingStock == null){
                return "보유한 주식이 없습니다.";
            }else {
                newStock = Stock.builder()
                        .user(user).id(existingStock.getId())
                        .stockId(requestDTO.getStockId()).stockName(requestDTO.getStockName())
                        .price(existingStock.getPrice()-(existingStock.getPrice()/existingStock.getQuantity()*requestDTO.getQuantity()))
                        .quantity(existingStock.getQuantity()-requestDTO.getQuantity())
                        .build();
            }
            long beforeQuantity = existingStock.getQuantity();
            long beforePrice = existingStock.getPrice();
            if(newStock.getQuantity()==0){
                stockRepository.delete(newStock);
            }else{
                Stock savedStock = stockRepository.save(newStock);
            }
            user.setMoney(user.getMoney()+ requestDTO.getPrice());
            User savedUser = userRepository.save(user);
            TradeHistory history = new TradeHistory(requestDTO,user,"sell");
            TradeHistory savedHistory = tradeHistoryRepository.save(history);

            long profit = (long) (((requestDTO.getPrice()/requestDTO.getQuantity())
                    -(beforePrice/beforeQuantity))*
                    requestDTO.getQuantity());
            System.out.println("profit:"+profit);
            if(rankingRepository.existsById(requestDTO.getEmail())){
            Ranking userRank = rankingRepository.findById(requestDTO.getEmail()).orElseThrow();
                profit += userRank.getProfit();
            }
            rankingRepository.save(Ranking.builder().userName(user.getName()).profit(profit).email(user.getEmail()).build());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    public List<TradeHistory> getTradeHistory(String email){
        User user = userRepository.findByEmail(email).orElseThrow();
        return tradeHistoryRepository.findByUser(user);
    }


    public List<RankResponseDTO> getAllRank() {
        List<Ranking> rankingList = rankingRepository.findAll(Sort.by(Sort.Direction.DESC,"profit"));
        List<RankResponseDTO> dtoList = new ArrayList<>();
        Long i = 1L;
        for (Ranking ranking : rankingList){
            RankResponseDTO dto = RankResponseDTO.builder().rank(i).userName(ranking.getUserName())
                    .email(ranking.getEmail()).profit(ranking.getProfit())
                    .build();
            dtoList.add(dto);
            i++;
        }
        return dtoList;
    }
}

