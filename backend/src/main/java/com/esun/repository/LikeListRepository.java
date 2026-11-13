package com.esun.repository;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.esun.domain.LikeList;
public interface LikeListRepository extends Repository<LikeList, Integer> {
    // 喜好清單查詢結果，包含商品資訊、手續費及總金額
    interface LikeListRow {
        Integer getSn();
        Integer getProductNo();
        String getProductName();
        BigDecimal getPrice();
        BigDecimal getFeeRate();
        Integer getPurchaseQuantity();
        String getAccount();
        BigDecimal getTotalFee();
        BigDecimal getTotalAmount();
        String getEmail();
    }
    // 查詢會員的喜好清單
    @Query(
        value = "EXEC FAVORITE.GET_LIKES @USER_ID = :userId",
        nativeQuery = true
    )
    List<LikeListRow> findLikes(
        @Param("userId") String userId
    );
    // 新增喜好商品
    @Procedure(procedureName = "FAVORITE.INSERT_LIKE")
    void insertLike(
        @Param("USER_ID") String userId,
        @Param("PRODUCT_NO") Integer productNo,
        @Param("PURCHASE_QUANTITY") Integer quantity,
        @Param("ACCOUNT") String account
    );
    // 修改喜好商品及購買資訊
    @Procedure(procedureName = "FAVORITE.UPDATE_LIKE")
    void updateLike(
        @Param("SN") Integer sn,
        @Param("USER_ID") String userId,
        @Param("PRODUCT_NO") Integer productNo,
        @Param("PURCHASE_QUANTITY") Integer quantity,
        @Param("ACCOUNT") String account
    );
    // 刪除喜好商品
    @Procedure(procedureName = "FAVORITE.DELETE_LIKE")
    void deleteLike(
        @Param("SN") Integer sn,
        @Param("USER_ID") String userId
    );
}
