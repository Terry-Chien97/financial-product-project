package com.esun.repository;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.esun.domain.Product;
public interface ProductRepository extends Repository<Product, Integer> {
    // 查詢會員的所有金融商品
    @Query(
        value = "EXEC FINANCIAL.GET_PRODUCTS @USER_ID = :userId",
        nativeQuery = true
    )
    List<Product> findProducts(@Param("userId") String userId);
    // 新增金融商品
    @Procedure(procedureName = "FINANCIAL.INSERT_PRODUCT")
    void insertProduct(
        @Param("USER_ID") String userId,
        @Param("PRODUCT_NAME") String productName,
        @Param("PRICE") BigDecimal price,
        @Param("FEE_RATE") BigDecimal feeRate
    );
    // 修改金融商品
    @Procedure(procedureName = "FINANCIAL.UPDATE_PRODUCT")
    void updateProduct(
        @Param("NO") Integer no,
        @Param("USER_ID") String userId,
        @Param("PRODUCT_NAME") String productName,
        @Param("PRICE") BigDecimal price,
        @Param("FEE_RATE") BigDecimal feeRate
    );
    // 刪除金融商品
    @Procedure(procedureName = "FINANCIAL.DELETE_PRODUCT")
    void deleteProduct(
        @Param("NO") Integer no,
        @Param("USER_ID") String userId
    );
}
