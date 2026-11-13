package com.esun.domain;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PRODUCT", schema = "FINANCIAL")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NO")
    private Integer no;
    @Column(name = "USER_ID", length = 10, nullable = false)
    private String userId;
    @Column(name = "PRODUCT_NAME", length = 50, nullable = false)
    private String productName;
    @Column(name = "PRICE", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;
    @Column(name = "FEE_RATE", precision = 10, scale = 6, nullable = false)
    private BigDecimal feeRate;
}
