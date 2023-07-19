package com.finalProject.stockbeginner.trade.api;

import com.finalProject.stockbeginner.trade.dto.request.TradeRequestDTO;
import com.finalProject.stockbeginner.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/trade")
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<?> buying(@RequestBody TradeRequestDTO requestDTO){
        try {
            String result = tradeService.buyStock(requestDTO);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> selling(@RequestBody TradeRequestDTO requestDTO){
        try {
            String result = tradeService.sellStock(requestDTO);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
