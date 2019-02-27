package io.pillopl.bigballofmud.services;

import io.pillopl.bigballofmud.entities.BookEntity;
import io.pillopl.bigballofmud.entities.BookHolderEntity;
import io.pillopl.bigballofmud.exceptions.EntityNotFound;
import io.pillopl.bigballofmud.exceptions.InvalidBookCollectionStateException;
import io.pillopl.bigballofmud.exceptions.InvalidBookLendingStateException;
import io.pillopl.bigballofmud.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class BookService {

    private final BookHolderService bookHolderService;
    private final BookRepository bookRepository;
    private final MailService mailService;

    BookService(BookHolderService bookHolderService, BookRepository bookRepository, MailService mailService) {
        this.bookHolderService = bookHolderService;
        this.bookRepository = bookRepository;
        this.mailService = mailService;
    }

    @Transactional
    public void createHold(Integer days, boolean openEnded, UUID holderId, UUID bookId) {

        BookHolderEntity holder = bookHolderService.findHolder(holderId);
        BookEntity book = bookRepository.getOne(bookId);

        if(book == null || holder == null) {
            throw new EntityNotFound();
        }

        if (book.getState() == BookEntity.BookState.JustInCatalogue || book.getLendingState() != BookEntity.BookLendingState.Available) {
            throw new InvalidBookLendingStateException("book cannot be lend");
        } else {
            book.setState(BookEntity.BookState.InLending);
            book.setLendingState(BookEntity.BookLendingState.OnHold);
            book.setCollectedFrom(null);
            book.setCollectedTill(null);
            book.setOnHoldFrom(Instant.now());

            if (openEnded) {
                if (days != null) {
                    throw new IllegalArgumentException("open ended hold cannot be limited by number of days");
                }
            } else if (days == null || days <= 0) {
                throw new IllegalArgumentException("incorrect number of days for hold");
            } else {
                book.setOnHoldTill(Instant.now().plus(Duration.ofDays(days)));
            }

            bookHolderService.addHold(holderId, openEnded, book);



        }

        mailService.sendMail(holder.getEmail(), "you have just placed a book on hold");



    }


    @Transactional
    public void removeHold(UUID holderId, UUID bookId) {
        BookHolderEntity holder = bookHolderService.findHolder(holderId);
        BookEntity book = bookRepository.getOne(bookId);

        if(holder == null) {
            throw new IllegalArgumentException();
        }

        //if book == null?

        Optional<BookEntity> optionalBook = holder.getBooks().stream().filter(b -> b.getId().equals(bookId))
                .findAny();

        if(!optionalBook.isPresent()) {
            throw new InvalidBookCollectionStateException("Book not placed on Hold by patron?");
        } else {
            if(optionalBook.get().getState() == BookEntity.BookState.InLending && optionalBook.get().getLendingState() == BookEntity.BookLendingState.OnHold){
                holder.getBooks().remove(optionalBook.get());
                book.setBookHolderEntity(null);
            } else {
                throw new IllegalArgumentException();
            }

        }


    }

    @Transactional
    public void changeDesc(UUID bookId, String newTitle, String author) {
        BookEntity book = bookRepository.getOne(bookId);
        if(book == null ) {
            throw new EntityNotFound();
        }

        if(newTitle == null || newTitle.isEmpty() || newTitle.length() > 100) {
            throw new IllegalArgumentException("Invalid title");
        }


        if(author == null || author.isEmpty() || author.length() > 60) {
            throw new IllegalArgumentException("Invalid author");
        }

        book.setTitle(newTitle);
        book.setAuthor(author);

    }

    public void createBook(boolean canBeInLending, String title, String isbn, String author, BigDecimal pricePerDay, boolean isCirulating) {
        BookEntity book = new BookEntity();

        if(canBeInLending) {
            book.setState(BookEntity.BookState.InLending);
            book.setLendingState(BookEntity.BookLendingState.Available);
        } else {
            book.setState(BookEntity.BookState.JustInCatalogue);
            book.setType(null);
            book.setLendingState(null);
            book.setLendingCostPerDay(null);
        }

        if(canBeInLending && pricePerDay == null) {
            throw new InvalidBookLendingStateException("book that is in lending cannot have null price");
        } else if (canBeInLending) {
            book.setLendingCostPerDay(pricePerDay);
        }

        if(title == null || title.isEmpty() || title.length() > 100) {
            throw new IllegalArgumentException("Invalid title");
        }

        if(author == null || author.isEmpty() || author.length() > 60) {
            throw new IllegalArgumentException("Invalid author");
        }

        if(isbn == null || isbn.isEmpty() || isbn.length() > 600) {
            throw new IllegalArgumentException("Invalid isbn");
        }

        if(!isbn.matches("^\\d{9}[\\d|X]$")) {
            throw new IllegalArgumentException("Invalid isbn");
        }

        if(canBeInLending && isCirulating) {
            book.setType(BookEntity.BookType.Circulating);
        } else if(canBeInLending) {
            book.setType(BookEntity.BookType.Restricted);
        }

        bookRepository.save(book);


    }

    public void changeBookState(String isbn, UUID bookId, BookEntity.BookState state, BigDecimal pricePerDay, BookEntity.BookLendingState lendingState) {
        BookEntity book = bookRepository.getOne(bookId);

        if(book == null ) {
            throw new EntityNotFound();
        }

        if(state == BookEntity.BookState.InLending) {
            if(pricePerDay == null) {
                throw new InvalidBookLendingStateException("book that is in lending cannot have null price");
            }

            if(lendingState == null) {
                throw new InvalidBookLendingStateException("Lending state cannot be null when book is in lending");
            }

            book.setState(BookEntity.BookState.InLending);
            book.setLendingCostPerDay(pricePerDay);
            book.setLendingState(lendingState);

        }

        if(state == BookEntity.BookState.JustInCatalogue) {
            if(lendingState != null) {
                throw new InvalidBookLendingStateException("Lending state cannot be defined when book is in catalogue");
            }

            book.setLendingState(null);
            book.setState(BookEntity.BookState.JustInCatalogue);
            book.setLendingCostPerDay(null);
            book.setOnHoldTill(null);
            book.setCollectedTill(null);
            book.setOnHoldFrom(null);
            book.setCollectedFrom(null);

        }

        if(isbn == null || isbn.isEmpty() || isbn.length() > 600) {
            throw new IllegalArgumentException("Invalid isbn");
        }

        if(!isbn.matches("^\\d{9}[\\d|X]$")) {
            throw new IllegalArgumentException("Invalid isbn");
        }

        book.setIsbn(isbn);
    }


}
