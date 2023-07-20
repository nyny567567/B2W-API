package com.finalProject.stockbeginner.user.repository;

import com.finalProject.stockbeginner.user.entity.FavoriteStock;
import com.finalProject.stockbeginner.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FavoriteStockRepository extends JpaRepository<FavoriteStock, String> {

    @Query("SELECT COUNT(*) FROM FavoriteStock f WHERE f.stockCode = :stockCode AND f.user = :user")
    Integer existsByUserAndStock(@Param("user") User user,@Param("stockCode") String stockCode);

    List<FavoriteStock> findByUserAndStockCode(@Param("user") User user,@Param("stockCode") String stockCode);

//    @Query("SELECT f.stockCode, f.stockName FROM FavoriteStock f WHERE f.user = :user")
    List<FavoriteStock> findByUser(@Param("user") User user);
}
