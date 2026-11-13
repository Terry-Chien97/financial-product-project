package com.esun.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esun.domain.Member;
import com.esun.dto.ChangePasswordDto;
import com.esun.dto.MemberDto;
import com.esun.jwt.JsonWebTokenUtility;
import com.esun.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JsonWebTokenUtility jsonWebTokenUtility;
    // 檢查 USER_ID 是否存在
    public boolean checkUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(
                memberRepository.checkUserId(
                        userId.trim().toUpperCase()));
    }
    // 檢查 Email 是否存在
    public boolean checkEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(
                memberRepository.checkEmail(
                        email.trim(),
                        null));
    }
    // 註冊
    @Transactional
    public void insertMember(MemberDto dto) {
        String userId = dto.getUserId()
                .trim()
                .toUpperCase();
        String userName = dto.getUserName()
                .trim();
        String email = dto.getEmail()
                .trim();
        String account = dto.getAccount()
                .trim();
        if (checkUserId(userId)) {
            throw new IllegalArgumentException(
                    "身分證字號已存在");
        }
        if (checkEmail(email)) {
            throw new IllegalArgumentException(
                    "Email 已被使用");
        }
        String passwordHash =
                passwordEncoder.encode(
                        dto.getPassword());
        memberRepository.insertMember(
                userId,
                userName,
                email,
                account,
                passwordHash);
    }
    // 登入
    public String login(MemberDto dto) {
        String userId = dto.getUserId()
                .trim()
                .toUpperCase();
        Member member = memberRepository
                .findMember(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "身分證字號或密碼錯誤"));
        if (!passwordEncoder.matches(
                dto.getPassword(),
                member.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "身分證字號或密碼錯誤");
        }
        return jsonWebTokenUtility.createToken(userId);
    }
    // 查詢個人資料
    public MemberDto getProfile(String userId) {
        Member member = memberRepository
                .findMember(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "使用者不存在"));
        return MemberDto.builder()
                .userId(member.getUserId())
                .userName(member.getUserName())
                .email(member.getEmail())
                .account(member.getAccount())
                .build();
    }
    // 修改個人資料
    @Transactional
    public void updateMember(
            String userId,
            MemberDto dto) {
        memberRepository
                .findMember(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "使用者不存在"));
        String userName = dto.getUserName()
                .trim();
        String email = dto.getEmail()
                .trim();
        String account = dto.getAccount()
                .trim();
        boolean emailExists =
                Boolean.TRUE.equals(
                        memberRepository.checkEmail(
                                email,
                                userId));
        if (emailExists) {
            throw new IllegalArgumentException(
                    "Email 已被使用");
        }
        memberRepository.updateMember(
                userId,
                userName,
                email,
                account);
    }
    // 修改密碼
    @Transactional
    public void updatePassword(String userId, ChangePasswordDto dto) {
        if (dto == null || dto.getCurrentPassword() == null
                || dto.getCurrentPassword().isEmpty()) {
            throw new IllegalArgumentException("請輸入目前密碼");
        }
        String newPassword = dto.getNewPassword();
        if (newPassword == null || newPassword.length() < 6
                || newPassword.length() > 15) {
            throw new IllegalArgumentException("新密碼須為 6～15 碼");
        }
        Member member = memberRepository.findMember(userId)
                .orElseThrow(() -> new IllegalArgumentException("使用者不存在"));
        if (!passwordEncoder.matches(
                dto.getCurrentPassword(), member.getPasswordHash())) {
            throw new IllegalArgumentException("目前密碼錯誤");
        }
        if (passwordEncoder.matches(newPassword, member.getPasswordHash())) {
            throw new IllegalArgumentException("新密碼不可與目前密碼相同");
        }
        memberRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
    }
}
