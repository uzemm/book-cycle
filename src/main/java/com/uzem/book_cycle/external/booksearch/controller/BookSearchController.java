package com.uzem.book_cycle.external.booksearch.controller;

import com.uzem.book_cycle.external.booksearch.dto.BookSearchDTO;
import com.uzem.book_cycle.external.booksearch.service.BookSearchService;
import com.uzem.book_cycle.exception.BookSearchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.uzem.book_cycle.external.booksearch.type.BookSearchErrorCode.EMPTY_SEARCH_QUERY;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookSearchController {

    private final BookSearchService bookService;

    @GetMapping("/search-form")
    @PreAuthorize("isAuthenticated()")
    public String searchForm() {
        return "book-search";
    }


    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public String search(@RequestParam("query") String query, Model model) {
        if (query == null || query.trim().isEmpty()) {
            throw new BookSearchException(EMPTY_SEARCH_QUERY);
        }

        List<BookSearchDTO> books = bookService.searchBook(query);
        model.addAttribute("books", books);

        return "book-list";
    }

}

