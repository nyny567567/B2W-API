package com.finalProject.stockbeginner.user.dto.response;

import com.finalProject.stockbeginner.trade.entity.Stock;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.entity.UserRole;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyInfoResponseDTO {

    private String email;
    private String name;
    private String nick;
    private Integer age;
    private String career;
    private Long money;
    private String gender;
    private List<MyStock> myStocks;
    private String mbti;
    private UserRole role;

    public MyInfoResponseDTO(User user, List<Stock> stocks){
        ArrayList<MyStock> myStocks = new ArrayList<>();
        stocks.forEach(stock -> {
            MyStock myStock = MyStock.builder()
                    .stockId(stock.getStockId())
                    .stockName(stock.getStockName())
                    .price(stock.getPrice())
                    .quantity(stock.getQuantity())
                    .build();
            myStocks.add(myStock);
        });

        this.email = user.getEmail();
        this.name = user.getName();
        this.nick = user.getNick();
        this.age = user.getAge();
        this.career = user.getCareer();
        this.money = user.getMoney();
        this.gender = user.getGender();
        this.myStocks = myStocks;
        this.mbti = user.getMbti();
        this.role = user.getUserRole();
    }

}

