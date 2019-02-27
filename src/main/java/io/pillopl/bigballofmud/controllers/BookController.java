package io.pillopl.bigballofmud.controllers;


import io.pillopl.acl.LendingACL;
import io.pillopl.bigballofmud.dtos.BookDto;
import io.pillopl.bigballofmud.dtos.BookRequest;
import io.pillopl.bigballofmud.entities.BookEntity;
import io.pillopl.bigballofmud.services.BookHolderService;
import io.pillopl.bigballofmud.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Controller
public class BookController {


    private final LendingACL lendingACL;
    private final BookService bookService;
    private final BookHolderService bookHolderService;

    public BookController(LendingACL lendingACL, BookService bookService, BookHolderService bookHolderService) {
        this.lendingACL = lendingACL;
        this.bookService = bookService;
        this.bookHolderService = bookHolderService;
    }

    @PostMapping("/books/holds")
    public ResponseEntity addHold(@RequestBody BookRequest bookRequest) {
        bookService.createHold(bookRequest.getDays(), bookRequest.isOpenEndedHold(), bookRequest.getHolderId(), bookRequest.getBookId());
        lendingACL.createHold(bookRequest.getDays(), bookRequest.isOpenEndedHold(), bookRequest.getHolderId(), bookRequest.getBookId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/books/collections")
    @Transactional
    public ResponseEntity collect(@RequestBody BookRequest bookRequest) {
        bookService.removeHold(bookRequest.getHolderId(), bookRequest.getBookId());
        bookHolderService.createCollectedBook(bookRequest.getHolderId(), bookRequest.getBookId(), bookRequest.getDays());
        lendingACL.collect(bookRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/books")
    @Transactional
    public ResponseEntity changeState(@RequestBody BookDto bookDto) {
        bookService.changeBookState(bookDto.getIsbn(), bookDto.getBookId(), bookDto.getBookState(), bookDto.getPricePerDay(), bookDto.getBookLendingState());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/titles")
    @Transactional
    public ResponseEntity changeDescription(@RequestBody BookDto bookDto) {
        bookService.changeDesc(bookDto.getBookId(), bookDto.getTitle(), bookDto.getAuthor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/collectBook")
    //return book to a library
    public ResponseEntity removeCollectedBook(@RequestBody BookRequest bookRequest) {
        bookHolderService.removeCollectedBook(bookRequest.getHolderId(), bookRequest.getBookId());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/holds/{holderId}")
    @Transactional
    public ResponseEntity<List<BookDto>> getPlacedOnHoldBooks(@RequestParam UUID holderId) {
        Set<BookEntity> books = bookHolderService.getBooks(holderId);
        List<BookDto> oldModel = books.stream().map(BookDto::from).filter(dto -> dto.getBookLendingState() == BookEntity.BookLendingState.OnHold).collect(toList());
        return ResponseEntity.ok(lendingACL.booksPlacedOnHoldBy(holderId, oldModel));

    }

    @GetMapping("/books/{holderId}")
    @Transactional
    public ResponseEntity<List<BookDto>> getCollectedBooks(@RequestParam UUID holderId) {
        Set<BookEntity> books = bookHolderService.getBooks(holderId);
        List<BookDto> oldModel = books.stream().map(BookDto::from).filter(dto -> dto.getBookLendingState() == BookEntity.BookLendingState.Collected).collect(toList());
        return ResponseEntity.ok(lendingACL.booksCurrentlyCollectedBy(holderId, oldModel));

    }
}


