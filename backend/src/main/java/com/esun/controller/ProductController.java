package com.esun.controller;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esun.dto.ProductDto;
import com.esun.service.ProductService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @GetMapping
    public List<ProductDto> getProducts(Authentication authentication) {
        return productService.getProducts(authentication.getName());
    }
    @PostMapping
    public ResponseEntity<Map<String, String>> insertProduct(
            Authentication authentication,
            @RequestBody ProductDto dto) {
        productService.insertProduct(authentication.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "產品新增成功"));
    }
    @PutMapping("/{no}")
    public ResponseEntity<Map<String, String>> updateProduct(
            Authentication authentication,
            @PathVariable Integer no,
            @RequestBody ProductDto dto) {
        productService.updateProduct(authentication.getName(), no, dto);
        return ResponseEntity.ok(Map.of("message", "產品修改成功"));
    }
    @DeleteMapping("/{no}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            Authentication authentication,
            @PathVariable Integer no) {
        productService.deleteProduct(authentication.getName(), no);
        return ResponseEntity.ok(Map.of("message", "產品刪除成功"));
    }
}
