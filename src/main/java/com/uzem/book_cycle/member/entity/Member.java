package com.uzem.book_cycle.member.entity;

import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.member.type.MemberStatus;
import com.uzem.book_cycle.member.type.Role;
import com.uzem.book_cycle.member.type.SocialType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.uzem.book_cycle.member.type.MemberErrorCode.INSUFFICIENT_POINTS;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentalHistory> rentalHistories = new ArrayList<>();

    public void activateMember(){

        this.status = MemberStatus.ACTIVE;
    }

    public void updateMyInfo(
            String phone, String address) {
        this.phone = phone;
        this.address = address;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void usePoint(long point) {
        if(this.point < point){
            throw new MemberException(INSUFFICIENT_POINTS);
        }
        this.point -= point;
    }

    public void rewardPoint(long point) {
        this.point += point;
    }

    public void rentalCnt() {
        this.rentalCnt += 1;
    }

    public void returnRentalCnt() {
        this.rentalCnt = 0;
    }

    public void addRentalHistory(RentalHistory rentalHistory) {
        rentalHistories.add(rentalHistory);
        rentalHistory.setMember(this);
    }

    public void removeRentalHistory(RentalHistory rentalHistory) {
        rentalHistories.remove(rentalHistory);
        rentalHistory.setMember(null);
    }

    public void deleteMember(){
        this.isDeleted = true;
        this.point = 0L;
    }

    public void cancelOrder(Long rewardPoint, Long usedPoint, int rentalCnt){
        // 적립 포인트 회수, 차감 포인트 복원
        this.point = Math.max(0, this.point + usedPoint - rewardPoint);
        // 대여 횟수 복원
        this.rentalCnt = Math.max(0, this.rentalCnt - rentalCnt);
    }
}
