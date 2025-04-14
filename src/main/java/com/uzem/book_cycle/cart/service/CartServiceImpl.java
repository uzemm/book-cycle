package com.uzem.book_cycle.cart.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.admin.repository.SalesRepository;
import com.uzem.book_cycle.cart.dto.CartRequestDTO;
import com.uzem.book_cycle.cart.dto.CartResponseDTO;
import com.uzem.book_cycle.cart.entity.Cart;
import com.uzem.book_cycle.cart.repository.CartRepository;
import com.uzem.book_cycle.exception.CartException;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.exception.RentalException;
import com.uzem.book_cycle.exception.SalesException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalErrorCode.RENTAL_BOOK_NOT_FOUND;
import static com.uzem.book_cycle.admin.type.RentalStatus.*;
import static com.uzem.book_cycle.admin.type.SalesErrorCode.SALES_BOOK_NOT_FOUND;
import static com.uzem.book_cycle.admin.type.SalesStatus.SOLD;
import static com.uzem.book_cycle.cart.type.CartErrorCode.*;
import static com.uzem.book_cycle.member.type.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.uzem.book_cycle.order.type.ItemType.SALE;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final AdminRentalRepository rentalRepository;
    private final SalesRepository salesRepository;
    private final MemberRepository memberRepository;

    @Override
    public CartResponseDTO addCart(CartRequestDTO request, Long memberId) {
        Member member = findByMemberId(memberId); // 회원 조회
        // 판매 도서
        if(request.getItemType() == SALE) {
            SalesBook salesBook = findBySalesBookId(request.getBookId());
            if(salesBook.getSalesStatus() == SOLD){ // 품절이면
                throw new CartException(SOLD_OUT_BOOK_CART_ADD_FAILED);
            }
            validDuplicateCartItem(request, memberId); // 중복 검사
            Cart cart = saveCart(request, member);
            return CartResponseDTO.fromSales(cart, salesBook);
        } else{ // 대여 도서
            RentalBook rentalBook = findByRentalBookId(request.getBookId());
            validCartRentalBook(request, memberId, rentalBook);
            Cart cart = saveCart(request, member);
            return CartResponseDTO.fromRental(cart, rentalBook);
        }
    }

    private void validCartRentalBook(CartRequestDTO request, Long memberId,
                                     RentalBook rentalBook) {
        // 중복 검사
        validDuplicateCartItem(request, memberId);
        // 대여 or 연체
        if(EnumSet.of(RENTED, OVERDUE).contains(rentalBook.getRentalStatus())){
            throw new CartException(RENTED_BOOK_CART_ADD_FAILED);
        }
        // 결제 대기 도서 내 예약인지 검증
        if(rentalBook.getRentalStatus() == PENDING_PAYMENT &&
                !rentalBook.getReservation().getMember().getId().equals(memberId)) {
            throw new CartException(RESERVATION_NOT_OWNED);
        }
    }

    private void validDuplicateCartItem(CartRequestDTO request, Long memberId) {
        boolean isDuplicate =
                cartRepository.existsByMemberIdAndBookId(memberId, request.getBookId());
        if(isDuplicate){
            throw new CartException(DUPLICATE_CART_ITEM);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartResponseDTO> getCartList(Long memberId) {
        Member member = findByMemberId(memberId);
        List<CartResponseDTO> result = new ArrayList<>();

        if(member==null){
            throw new MemberException(MEMBER_NOT_FOUND);
        }
        List<Cart> cartList = cartRepository.findByMember(member);

        for (Cart cart : cartList) {
            if(cart.getItemType() == SALE){
                SalesBook salesBook = findBySalesBookId(cart.getBookId());
                result.add(CartResponseDTO.fromSales(cart,salesBook));
            } else{
                RentalBook rentalBook = findByRentalBookId(cart.getBookId());
                result.add(CartResponseDTO.fromRental(cart, rentalBook));
            }
        }
        return result;
    }

    @Override
    public void deleteCart(Long cartId, Long memberId) {
        Cart cart = cartRepository.findByIdAndMemberId(cartId, memberId).orElseThrow(
                () -> new CartException(CART_NOT_FOUND));
        cartRepository.delete(cart);
    }

    @Override
    public void deleteCarts(List<Long> cartIds, Long memberId) {
        List<Cart> carts = cartRepository.findAllByIdInAndMemberId(cartIds, memberId);
        if(carts.size() != cartIds.size()){
            throw new CartException(CART_NOT_FOUND);
        }
        cartRepository.deleteAll(carts);
    }

    private Cart saveCart(CartRequestDTO request, Member member) {
        Cart cart = Cart.from(request.getBookId(), request.getItemType(), member);
        return cartRepository.save(cart);
    }

    private SalesBook findBySalesBookId(Long bookId) {
        return salesRepository.findById(bookId).orElseThrow(
                () -> new SalesException(SALES_BOOK_NOT_FOUND));
    }

    private RentalBook findByRentalBookId(Long bookId) {
        RentalBook rentalBook = rentalRepository.findById(bookId).orElseThrow(
                () -> new RentalException(RENTAL_BOOK_NOT_FOUND));
        return rentalBook;
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));
    }

}
