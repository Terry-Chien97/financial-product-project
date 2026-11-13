package com.esun.service;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esun.dto.LikeListDto;
import com.esun.repository.LikeListRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeListService {
    private final LikeListRepository likeListRepository;
    public List<LikeListDto> getLikes(String userId) {
        return likeListRepository.findLikes(userId)
            .stream()
            .map(row -> LikeListDto.builder()
                .sn(row.getSn())
                .productNo(row.getProductNo())
                .productName(row.getProductName())
                .price(row.getPrice())
                .feeRate(row.getFeeRate())
                .purchaseQuantity(row.getPurchaseQuantity())
                .account(row.getAccount())
                .totalFee(row.getTotalFee())
                .totalAmount(row.getTotalAmount())
                .email(row.getEmail())
                .build())
            .toList();
    }
    @Transactional
    public void insertLike(String userId, LikeListDto dto) {
        likeListRepository.insertLike(
            userId,
            dto.getProductNo(),
            dto.getPurchaseQuantity(),
            dto.getAccount()
        );
    }
    @Transactional
    public void updateLike(String userId,Integer sn,LikeListDto dto
    ) {
        likeListRepository.updateLike(
            sn,
            userId,
            dto.getProductNo(),
            dto.getPurchaseQuantity(),
            dto.getAccount()
        );
    }
    @Transactional
    public void deleteLike(String userId, Integer sn) {
        likeListRepository.deleteLike(sn, userId);
    }
}
