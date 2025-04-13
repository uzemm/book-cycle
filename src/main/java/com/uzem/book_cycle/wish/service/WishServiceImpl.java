package com.uzem.book_cycle.wish.service;

import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.admin.repository.SalesRepository;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.exception.SalesException;
import com.uzem.book_cycle.exception.WishException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.wish.dto.WishResponseDTO;
import com.uzem.book_cycle.wish.entity.Wish;
import com.uzem.book_cycle.wish.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.uzem.book_cycle.admin.type.SalesErrorCode.SALES_BOOK_NOT_FOUND;
import static com.uzem.book_cycle.member.type.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.uzem.book_cycle.wish.type.WishErrorCode.ALREADY_ADDED_TO_WISH;
import static com.uzem.book_cycle.wish.type.WishErrorCode.WISH_BOOK_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class WishServiceImpl implements WishService {

    private final SalesRepository salesRepository;
    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;

    // 관심도서 추가
    @Override
    public WishResponseDTO addWishBook(Long salesBookId, Long memberId) {
        Member member = findByMemberId(memberId);
        // 판매도서 조회
        SalesBook salesBook = salesRepository.findById(salesBookId).orElseThrow(
                () -> new SalesException(SALES_BOOK_NOT_FOUND));

        // 중복 방지
        if(wishRepository.existsByMemberAndSalesBook(member, salesBook)){
            throw new WishException(ALREADY_ADDED_TO_WISH);
        }

        Wish wish = Wish.from(salesBook, member);
        wishRepository.save(wish);

        return WishResponseDTO.from(wish);
    }

    // 관심도서 조회
    @Override
    @Transactional(readOnly = true)
    public List<WishResponseDTO> getWishBookList(Long memberId) {
        Member member = findByMemberId(memberId);
        if(member==null){
            throw new MemberException(MEMBER_NOT_FOUND);
        }
        List<Wish> wish = wishRepository.findByMember(member);

        return wish.stream()
                .map(WishResponseDTO::from)
                .toList();
    }

    @Override
    public void deleteWishBook(Long salesBookId, Long memberId) {
        Wish wish = wishRepository.findByMemberIdAndSalesBookId(memberId, salesBookId)
                .orElseThrow(() -> new WishException(WISH_BOOK_NOT_FOUND));
        wishRepository.delete(wish);
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));
    }
}
