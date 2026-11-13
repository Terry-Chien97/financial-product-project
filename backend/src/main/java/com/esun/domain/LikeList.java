package com.esun.domain;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "LIKE_LIST", schema = "FAVORITE")
public class LikeList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SN")
    private Integer sn;
    @Column(name = "PRODUCT_NO", nullable = false)
    private Integer productNo;
    @Column(name = "PURCHASE_QUANTITY", nullable = false)
    private Integer purchaseQuantity;
    @Column(name = "ACCOUNT", length = 20, nullable = false)
    private String account;
    @Column(name = "TOTAL_FEE", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalFee;
    @Column(name = "TOTAL_AMOUNT", precision = 14, scale = 2, nullable = false)
    private BigDecimal totalAmount;
}
