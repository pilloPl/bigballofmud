package io.pillopl.bigballofmud.dtos;

import io.pillopl.bigballofmud.entities.BookEntity;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode(of = "bookId")
public class BookDto {

    private String title;
    private String author;
    private String isbn;
    private BigDecimal pricePerDay; //does not seem to be used in UI when showing collected/placedOnH books
    private UUID bookId;
    private BookEntity.BookState bookState;
    private BookEntity.BookLendingState bookLendingState;

    public BookDto() {
    }

    public static BookDto from(BookEntity entity) {
        BookDto dto = new BookDto();
        dto.setTitle(entity.getTitle());
        dto.setAuthor(entity.getAuthor());
        dto.setIsbn(entity.getIsbn());
        dto.setPricePerDay(entity.getLendingCostPerDay());
        dto.setBookId(entity.getId());
        dto.setBookState(entity.getState());
        dto.setBookLendingState(entity.getLendingState());
        return dto;
    }


    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public BigDecimal getPricePerDay() {
        return this.pricePerDay;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public BookEntity.BookState getBookState() {
        return bookState;
    }

    public void setBookState(BookEntity.BookState bookState) {
        this.bookState = bookState;
    }

    public BookEntity.BookLendingState getBookLendingState() {
        return bookLendingState;
    }

    public void setBookLendingState(BookEntity.BookLendingState bookLendingState) {
        this.bookLendingState = bookLendingState;
    }


}
