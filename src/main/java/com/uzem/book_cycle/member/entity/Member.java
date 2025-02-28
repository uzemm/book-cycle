package com.uzem.book_cycle.member.entity;

import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.member.type.MemberStatus;
import com.uzem.book_cycle.member.type.Role;
import com.uzem.book_cycle.member.type.SocialType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Member extends BaseEntity {

    @Column(unique = true, nullable = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private int rentalCnt;

    private Long point;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(unique = true)
    private String socialId;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;


    public void encodePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
    }

}
