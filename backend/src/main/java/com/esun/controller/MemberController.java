package com.esun.controller;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esun.dto.ChangePasswordDto;
import com.esun.dto.MemberDto;
import com.esun.service.MemberService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody MemberDto dto) {
        memberService.insertMember(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "註冊成功"));
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody MemberDto dto) {
        try {
            String token = memberService.login(dto);
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "message", "登入成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "身分證字號或密碼錯誤"));
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<MemberDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(memberService.getProfile(authentication.getName()));
    }
    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(
            Authentication authentication,
            @RequestBody MemberDto dto) {
        memberService.updateMember(authentication.getName(), dto);
        return ResponseEntity.ok(Map.of("message", "個人資料修改成功"));
    }
    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> updatePassword(
            Authentication authentication,
            @RequestBody ChangePasswordDto dto) {
        memberService.updatePassword(authentication.getName(), dto);
        return ResponseEntity.ok(Map.of("message", "密碼修改成功，請重新登入"));
    }
}
