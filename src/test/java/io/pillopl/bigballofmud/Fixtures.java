package io.pillopl.bigballofmud;

import io.pillopl.bigballofmud.entities.BookEntity;
import io.pillopl.bigballofmud.entities.BookHolderEntity;
import io.pillopl.bigballofmud.repositories.BookHolderRepository;
import io.pillopl.bigballofmud.repositories.BookRepository;
import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.book.Book;
import io.pillopl.newmodel.lending.domain.book.BookType;
import io.pillopl.newmodel.lending.domain.patron.PatronId;
import io.pillopl.newmodel.lending.domain.patron.PatronRepository;
import io.pillopl.newmodel.lending.domain.patron.PatronType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static io.pillopl.bigballofmud.entities.BookEntity.BookLendingState.Available;
import static io.pillopl.bigballofmud.entities.BookEntity.BookState.InLending;
import static io.pillopl.bigballofmud.entities.BookEntity.BookType.Circulating;
import static io.pillopl.bigballofmud.entities.BookEntity.BookType.Restricted;
import static io.pillopl.newmodel.lending.domain.patron.Patron.patron;

@Component
class Fixtures {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookHolderRepository bookHolderRepository;


    @Autowired
    io.pillopl.newmodel.lending.domain.book.BookRepository bookNewModelRepo;

    @Autowired
    PatronRepository patronRepository;

    BookHolderEntity aPatron(BookHolderEntity.HolderType type) {
        BookHolderEntity holder = new BookHolderEntity();
        holder.setType(type);
        holder.setHolderName("name");
        holder = bookHolderRepository.save(holder);
        if(type.equals(BookHolderEntity.HolderType.Regular)) {
            patronRepository.save(patron(PatronType.Regular, new PatronId(holder.getId())));
        } else {
            patronRepository.save(patron(PatronType.Researcher, new PatronId(holder.getId())));

        }
        return holder;
    }

    BookHolderEntity aRegularPatron() {
        return aPatron(BookHolderEntity.HolderType.Regular);
    }

    BookHolderEntity aResearcherPatron() {
        return aPatron(BookHolderEntity.HolderType.Researcher);
    }


    BookEntity aCirculatingBookAvailableForLending() {
        BookEntity book = new BookEntity();
        book.setIsbn("0198526636");
        book.setAuthor("author");
        book.setTitle("title");
        book.setState(InLending);
        book.setLendingState(Available);
        book.setType(Circulating);
        book.setLendingCostPerDay(BigDecimal.ZERO);
        book = bookRepository.save(book);
        bookNewModelRepo.save(new Book(new BookId(book.getId()), BookType.Circulating, Book.State.Available));
        return book;
    }


    BookEntity aRestrictedBookAvailableForLending() {
        BookEntity book = new BookEntity();
        book.setIsbn("0198526636");
        book.setAuthor("author");
        book.setTitle("title");
        book.setState(InLending);
        book.setLendingState(Available);
        book.setType(Restricted);
        book.setLendingCostPerDay(BigDecimal.ZERO);
        book = bookRepository.save(book);
        bookNewModelRepo.save(new Book(new BookId(book.getId()), BookType.Restricted, Book.State.Available));
        return book;
    }
}
