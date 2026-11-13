package com.esun.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.esun.domain.Member;
public interface MemberRepository
        extends Repository<Member, String> {
    // 檢查 USER_ID 是否存在
    @Procedure(
        procedureName = "MEMBER.CHECK_USER_ID",
        outputParameterName = "EXISTS"
    )
    Boolean checkUserId(
            @Param("USER_ID") String userId);
    // 檢查 Email 是否被使用
    @Procedure(
        procedureName = "MEMBER.CHECK_EMAIL",
        outputParameterName = "EXISTS"
    )
    Boolean checkEmail(
            @Param("EMAIL") String email,
            @Param("EXCLUDE_USER_ID") String excludeUserId);
    // 新增會員
    @Procedure(procedureName = "MEMBER.INSERT_MEMBER")
    void insertMember(
            @Param("USER_ID") String userId,
            @Param("USER_NAME") String userName,
            @Param("EMAIL") String email,
            @Param("ACCOUNT") String account,
            @Param("PASSWORD_HASH") String passwordHash);
    // 查詢會員
    @Query(
        value = "EXEC MEMBER.GET_MEMBER @USER_ID = :userId",
        nativeQuery = true
    )
    Optional<Member> findMember(@Param("userId") String userId);
    // 修改會員資料
    @Procedure(procedureName = "MEMBER.UPDATE_MEMBER")
    void updateMember(
            @Param("USER_ID") String userId,
            @Param("USER_NAME") String userName,
            @Param("EMAIL") String email,
            @Param("ACCOUNT") String account);
    // 修改密碼
    @Procedure(procedureName = "MEMBER.UPDATE_PASSWORD")
    void updatePassword(
            @Param("USER_ID") String userId,
            @Param("PASSWORD_HASH") String passwordHash);
}
