package com.finalProject.stockbeginner.trade.api;

import com.finalProject.stockbeginner.trade.dto.request.TradeRequestDTO;
import com.finalProject.stockbeginner.trade.dto.response.RankResponseDTO;
import com.finalProject.stockbeginner.trade.entity.TradeHistory;
import com.finalProject.stockbeginner.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/rank")
    public ResponseEntity<?> getAllRank(){
        try {
            List<RankResponseDTO> rankResponseDTOList = tradeService.getAllRank();
            return ResponseEntity.ok().body(rankResponseDTOList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/rank/{email}")
    public ResponseEntity<?> getOneRank(@PathVariable String email){
        try {
            List<RankResponseDTO> rankResponseDTOList = tradeService.getAllRank();
            Optional<RankResponseDTO> responseDTO = rankResponseDTOList.stream()
                    .filter(dto -> dto.getEmail().equals(email)).findFirst();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
