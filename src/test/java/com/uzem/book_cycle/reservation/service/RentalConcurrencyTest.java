package com.uzem.book_cycle.reservation.service;

import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.rental.service.RentalServiceImpl;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.order.repository.OrderRepository;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
public class RentalConcurrencyTest {

    @Autowired
    private RentalServiceImpl rentalService;
    @Autowired
    private AdminRentalRepository rentalRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderRepository orderRepository;



}
