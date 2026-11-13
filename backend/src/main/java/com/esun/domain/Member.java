package com.esun.domain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "USER_ACCOUNT", schema = "MEMBER")
public class Member {
    @Id
    @Column(name = "USER_ID", length = 10, nullable = false)
    private String userId;
    @Column(name = "USER_NAME", length = 10, nullable = false)
    private String userName;
    @Column(name = "EMAIL", length = 255, nullable = false)
    private String email;
    @Column(name = "ACCOUNT", length = 20, nullable = false)
    private String account;
    @Column(name = "PASSWORD_HASH", length = 60, nullable = false)
    private String passwordHash;
}
