package com.finalProject.stockbeginner.trade.api;

import com.finalProject.stockbeginner.trade.dto.request.TradeRequestDTO;
import com.finalProject.stockbeginner.trade.entity.TradeHistory;
import com.finalProject.stockbeginner.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/trade")
public class TradeController {

    private final TradeService tradeService;

    //매수 요청
    @PostMapping("/buy")
    public ResponseEntity<?> buying(@RequestBody TradeRequestDTO requestDTO){
        try {
            String result = tradeService.buyStock(requestDTO);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //매도 요청
    @PostMapping("/sell")
    public ResponseEntity<?> selling(@RequestBody TradeRequestDTO requestDTO){
        try {
            String result = tradeService.sellStock(requestDTO);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //구매내역 불러오기
    @GetMapping("/history/{email}")
    public ResponseEntity<?> getHistory(@PathVariable String email){
        try {
            List<TradeHistory> histories = tradeService.getTradeHistory(email);
            return ResponseEntity.ok().body(histories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
