package com.esun.service;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esun.dto.ProductDto;
import com.esun.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    public List<ProductDto> getProducts(String userId) {
        return productRepository.findProducts(userId)
            .stream()
            .map(product -> ProductDto.builder()
                .no(product.getNo())
                .productName(product.getProductName())
                .price(product.getPrice())
                .feeRate(product.getFeeRate())
                .build())
            .toList();
    }
    @Transactional
    public void insertProduct(String userId, ProductDto dto) {
        productRepository.insertProduct(
            userId,
            dto.getProductName().trim(),
            dto.getPrice(),
            dto.getFeeRate()
        );
    }
    @Transactional
    public void updateProduct(String userId, Integer no, ProductDto dto) {
        productRepository.updateProduct(
            no,
            userId,
            dto.getProductName().trim(),
            dto.getPrice(),
            dto.getFeeRate()
        );
    }
    @Transactional
    public void deleteProduct(String userId, Integer no) {
        productRepository.deleteProduct(no, userId);
    }
}
