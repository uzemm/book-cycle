package com.uzem.book_cycle.book.controller;

import com.uzem.book_cycle.book.dto.BookDTO;
import com.uzem.book_cycle.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping("/search-form")
    public String searchForm() {
        return "book-search";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<BookDTO> books = bookService.searchBook(query);
        model.addAttribute("books", books);

        return "book-list";
    }

}

