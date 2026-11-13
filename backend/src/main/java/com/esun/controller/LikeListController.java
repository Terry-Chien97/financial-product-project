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

import com.esun.dto.LikeListDto;
import com.esun.service.LikeListService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeListController {
    private final LikeListService likeListService;
    @GetMapping
    public List<LikeListDto> getLikes(Authentication authentication) {
        return likeListService.getLikes(authentication.getName());
    }
    @PostMapping
    public ResponseEntity<Map<String, String>> insertLike(
            Authentication authentication,
            @RequestBody LikeListDto dto) {
        likeListService.insertLike(authentication.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "喜好新增成功"));
    }
    @PutMapping("/{sn}")
    public ResponseEntity<Map<String, String>> updateLike(
            Authentication authentication,
            @PathVariable Integer sn,
            @RequestBody LikeListDto dto) {
        likeListService.updateLike(authentication.getName(), sn, dto);
        return ResponseEntity.ok(Map.of("message", "喜好修改成功"));
    }
    @DeleteMapping("/{sn}")
    public ResponseEntity<Map<String, String>> deleteLike(
            Authentication authentication,
            @PathVariable Integer sn) {
        likeListService.deleteLike(authentication.getName(), sn);
        return ResponseEntity.ok(Map.of("message", "喜好刪除成功"));
    }
}
