package io.pillopl.bigballofmud.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Entity
public class BookEntity {

    @Id @GeneratedValue
    private UUID id;

    public enum BookState {
        JustInCatalogue, InLending
    }

    public enum BookType {
        Restricted, Circulating
    }

    public enum BookLendingState {
        Available, OnHold, Collected
    }

    private String isbn;

    private BookState state;

    private BookType type;

    private BookLendingState lendingState;

    private String title;

    private String author;

    private Instant onHoldFrom;

    private Instant onHoldTill;

    private Instant collectedFrom;

    private Instant collectedTill;

    //can be changed?
    private BigDecimal lendingCostPerDay;

    @OneToOne
    private BookHolderEntity bookHolderEntity;

    @OneToOne
    private BookHolderEntity bookCollectedBy;

    public UUID getId() {
        return this.id;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public BookState getState() {
        return this.state;
    }

    public BookType getType() {
        return this.type;
    }

    public BookLendingState getLendingState() {
        return this.lendingState;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public Instant getOnHoldFrom() {
        return this.onHoldFrom;
    }

    public Instant getOnHoldTill() {
        return this.onHoldTill;
    }

    public Instant getCollectedFrom() {
        return this.collectedFrom;
    }

    public Instant getCollectedTill() {
        return this.collectedTill;
    }

    public BigDecimal getLendingCostPerDay() {
        return this.lendingCostPerDay;
    }

    public BookHolderEntity getBookHolderEntity() {
        return this.bookHolderEntity;
    }

    public BookHolderEntity getBookCollectedBy() {
        return this.bookCollectedBy;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setState(BookState state) {
        if(state == BookState.JustInCatalogue && lendingState != null) {
            throw new IllegalStateException();
        }
        this.state = state;
    }

    public void setType(BookType type) {
        this.type = type;
    }

    public void setLendingState(BookLendingState lendingState) {
        if(state == BookState.JustInCatalogue) {
            throw new IllegalStateException();
        }
        this.lendingState = lendingState;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setOnHoldFrom(Instant onHoldFrom) {
        this.onHoldFrom = onHoldFrom;
    }

    public void setOnHoldTill(Instant onHoldTill) {
        if(onHoldTill != null && onHoldTill.isBefore(onHoldFrom)) {
            throw new IllegalStateException();
        }
        this.onHoldTill = onHoldTill;
    }

    public void setCollectedFrom(Instant collectedFrom) {
        this.collectedFrom = collectedFrom;
    }

    public void setCollectedTill(Instant collectedTill) {
        if(collectedTill != null && collectedTill.isBefore(collectedFrom)) {
            throw new IllegalStateException();
        }
        this.collectedTill = collectedTill;
    }

    public void setLendingCostPerDay(BigDecimal lendingCostPerDay) {
        this.lendingCostPerDay = lendingCostPerDay;
    }

    public void setBookHolderEntity(BookHolderEntity bookHolderEntity) {
        this.bookHolderEntity = bookHolderEntity;
    }

    public void setBookCollectedBy(BookHolderEntity bookCollectedBy) {
        this.bookCollectedBy = bookCollectedBy;
    }




}
