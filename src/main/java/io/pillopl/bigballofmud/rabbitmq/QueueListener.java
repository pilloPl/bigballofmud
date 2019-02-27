package io.pillopl.bigballofmud.rabbitmq;


import io.pillopl.acl.LendingACL;
import io.pillopl.bigballofmud.dtos.BookRequest;
import io.pillopl.bigballofmud.services.BookHolderService;
import io.pillopl.bigballofmud.services.BookService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QueueListener {

    private final BookService bookService;
    private final BookHolderService bookHolderService;
    private final LendingACL lendingACL;


    public QueueListener(BookService bookService, BookHolderService bookHolderService, LendingACL lendingACL) {
        this.bookService = bookService;
        this.bookHolderService = bookHolderService;
        this.lendingACL = lendingACL;
    }

    @Transactional
    public boolean collect(BookRequest bookRequest) {
        try {
            bookService.removeHold(bookRequest.getHolderId(), bookRequest.getBookId());
            bookHolderService.createCollectedBook(bookRequest.getHolderId(), bookRequest.getBookId(), bookRequest.getDays());
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            lendingACL.collect(bookRequest);
        }

    }

}
