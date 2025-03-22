package com.uzem.book_cycle.naver.controller;

import com.uzem.book_cycle.naver.dto.BookDTO;
import com.uzem.book_cycle.naver.service.BookService;
import com.uzem.book_cycle.exception.BookException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.uzem.book_cycle.naver.type.BookErrorCode.EMPTY_SEARCH_QUERY;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping("/search-form")
    @PreAuthorize("isAuthenticated()")
    public String searchForm() {
        return "book-search";
    }


    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public String search(@RequestParam("query") String query, Model model) {
        if (query == null || query.trim().isEmpty()) {
            throw new BookException(EMPTY_SEARCH_QUERY);
        }

        List<BookDTO> books = bookService.searchBook(query);
        model.addAttribute("books", books);

        return "book-list";
    }

}

