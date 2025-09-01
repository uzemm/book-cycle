package com.uzem.book_cycle.reservation.service;

import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.rental.entity.RentalBook;
import com.uzem.book_cycle.rental.service.RentalServiceImpl;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

    @Test
    void 동시에_여러명이_대여시도하면_오직_한명만_성공한다() throws Exception{
        //given
        RentalBook rentalBook = rentalRepository.save(RentalBook.createTestBook());

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Future<Boolean>> results  = new ArrayList<>();

        //when

        for(int i = 0; i < threadCount; i ++){
            int idx = i;
            results.add(executor.submit(() -> {
                try {
                    Member member = memberRepository.save(Member.create("user@test.com"));
                    Order order = orderRepository.save(Order.create(member));
                    rentalService.createRentalHistory(
                            rentalBook.getId(), member.getId(), order.getId(), LocalDate.now());
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await();
        executor.shutdown();

        //then
        long successCount = results.stream().filter(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).count();
        long failCount = results.size() - successCount;


        assertThat(successCount).isEqualTo(1);
        assertThat(failCount).isEqualTo(threadCount - 1);
    }

}
