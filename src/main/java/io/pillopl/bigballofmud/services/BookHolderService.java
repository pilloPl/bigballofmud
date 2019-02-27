package io.pillopl.bigballofmud.services;


import io.pillopl.bigballofmud.entities.BookEntity;
import io.pillopl.bigballofmud.entities.BookHolderEntity;
import io.pillopl.bigballofmud.entities.HolderRentalFeeEntity;
import io.pillopl.bigballofmud.exceptions.EntityNotFound;
import io.pillopl.bigballofmud.exceptions.InvalidBookCollectionStateException;
import io.pillopl.bigballofmud.exceptions.InvalidBookLendingStateException;
import io.pillopl.bigballofmud.repositories.BookHolderRepository;
import io.pillopl.bigballofmud.repositories.BookRepository;
import io.pillopl.bigballofmud.repositories.HolderRentalFeeRepository;
import io.pillopl.bigballofmud.util.BooksCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Transactional
@Service
public class BookHolderService {

    private final BookHolderRepository bookHolderRepository;
    private final BookRepository bookRepository;
    private final HolderRentalFeeRepository holderRentalFeeRepository;

    public BookHolderService(BookHolderRepository bookHolderRepository, BookRepository bookRepository, HolderRentalFeeRepository holderRentalFeeRepository) {
        this.bookHolderRepository = bookHolderRepository;
        this.bookRepository = bookRepository;
        this.holderRentalFeeRepository = holderRentalFeeRepository;
    }


    public BookHolderEntity findHolder(UUID holderId) {
        return bookHolderRepository.getOne(holderId);
    }

    public void addHold(UUID holderId, boolean openEnded, BookEntity book) {
        BookHolderEntity holder = findHolder(holderId);

        if (holder.getType() == BookHolderEntity.HolderType.Regular) {
            if (openEnded) {
                throw new InvalidBookLendingStateException("open ended hold cannot be done for regular patron");
            }

            if (book.getType() == BookEntity.BookType.Restricted) {
                throw new InvalidBookLendingStateException("restricted books cannot be held by regular patron");
            }

        }

        if (holder.getBooks() != null && BooksCalculator.countOnHold(holder.getBooks()) >= 5) {
            throw new InvalidBookLendingStateException("regular patron can have up to 5 holds");
        }


        if (holder.getBooks() != null && BooksCalculator.collectedTill(holder.getBooks(), Instant.now()) >= 2) {
            throw new InvalidBookLendingStateException("more than 2 overdue books is not possible");
        }

        if(holder.getBooks() == null) {
            holder.setBooks(new HashSet<>());
        }

        holder.getBooks().add(book);

        if(book.getBookHolderEntity() != null) {
            throw new IllegalArgumentException("book is not on hold but there is a book holder, WTF?");

        }

        book.setBookHolderEntity(holder);
    }

    public void createCollectedBook(UUID holderId, UUID bookId, int days) {
        BookHolderEntity holder = bookHolderRepository.getOne(holderId);
        BookEntity book = bookRepository.getOne(bookId);

        if(book == null || holder == null) {
            throw new EntityNotFound();
        }

        if(book.getBookHolderEntity() != null && book.getBookHolderEntity().getId() != holderId) {
            throw new IllegalArgumentException();
        }

        book.setBookHolderEntity(null);
        book.setBookCollectedBy(holder);
        book.setLendingState(BookEntity.BookLendingState.Collected);

        if(days <= 0 || days > 60) {
            throw new InvalidBookCollectionStateException("Can be collected up to 60 days");
        }

        book.setCollectedFrom(Instant.now());
        book.setCollectedTill(Instant.now().plus(Duration.ofDays(days)));
        holder.getBooks().add(book);

        HolderRentalFeeEntity fee = holderRentalFeeRepository.findByBookHolderEntity(holder);

        if(fee == null) {
            HolderRentalFeeEntity entity = new HolderRentalFeeEntity();
            entity.setBookHolderEntity(holder);
            fee = holderRentalFeeRepository.save(entity);
        }


        //next big feature comes here - it will be many different strategies for calculating fees and discounts
        fee.setFee(book.getLendingCostPerDay().multiply(BigDecimal.valueOf(days)).add(fee.getFee()));

    }

    public void removeCollectedBook(UUID holderId, UUID bookId) {
        BookHolderEntity holder = bookHolderRepository.getOne(holderId);
        BookEntity book = bookRepository.getOne(bookId);

        if(book == null || holder == null) {
            throw new EntityNotFound();
        }
//
//        if(!book.getBookCollectedBy().getId().equals(holderId)) {
//            throw new IllegalArgumentException();
//        }

        book.setCollectedTill(null);
        book.setCollectedFrom(null);

        book.setBookCollectedBy(null);
        holder.getBooks().remove(book);
    }


    public Set<BookEntity> getBooks(UUID holderId) {
        BookHolderEntity holder = bookHolderRepository.getOne(holderId);
        if(holder == null) {
            throw new EntityNotFound();
        }
        return holder.getBooks();
    }
}
