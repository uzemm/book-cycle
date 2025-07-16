package com.uzem.book_cycle.book.policy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OverduePolicyTest {
    OverduePolicy policy = new OverduePolicy();

    @Test
    void calculateOverdueFee_within7Days_returnsZero(){
        long fee = policy.calculateOverdue(7L);
        assertThat(fee).isEqualTo(0L);
    }

    @Test
    void calculateOverdueFee_onDay8_returns500(){
        long fee = policy.calculateOverdue(8L);
        assertThat(fee).isEqualTo(500L);
    }

    @Test
    void calculateOverdueFee_onDay9_returns1000(){
        long fee = policy.calculateOverdue(9L);
        assertThat(fee).isEqualTo(1000L);
    }
}