package com.esun.dto;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeListDto {
    private Integer sn;
    private Integer productNo;
    private String productName;
    private BigDecimal price;
    private BigDecimal feeRate;
    private Integer purchaseQuantity;
    private String account;
    private BigDecimal totalFee;
    private BigDecimal totalAmount;
    private String email;
}
