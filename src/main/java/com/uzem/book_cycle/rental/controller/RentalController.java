package com.uzem.book_cycle.rental.controller;

import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.rental.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final AdminRentalRepository adminRentalRepository;

}
