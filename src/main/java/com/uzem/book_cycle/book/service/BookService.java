package com.uzem.book_cycle.book.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzem.book_cycle.book.dto.BookDTO;
import com.uzem.book_cycle.book.dto.BookResponseDTO;
import com.uzem.book_cycle.exception.BookException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.uzem.book_cycle.book.type.BookErrorCode.NAVER_API_ERROR;

@Service
public class BookService {

    private final String CLIENT_ID = "네이버_클라이언트_ID";
    private final String CLIENT_SECRET = "네이버_클라이언트_Secret";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    public List<BookDTO> searchBook(String query) throws BookException {
        try{
            String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + query  + "&display=10&sort=sim";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", CLIENT_ID);
            headers.set("X-Naver-Client-Secret", CLIENT_SECRET);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

            if(response.getStatusCode() == HttpStatus.OK){
                // JSON을 BookResponseDTO 객체로 변환
                BookResponseDTO bookResponseDTO = objectMapper.readValue(response.getBody(), BookResponseDTO.class);
                return bookResponseDTO.getItems();
            } else {
                throw new BookException(NAVER_API_ERROR);
            }
        } catch (Exception e) {
            throw new BookException(NAVER_API_ERROR);
        }
    }
}
