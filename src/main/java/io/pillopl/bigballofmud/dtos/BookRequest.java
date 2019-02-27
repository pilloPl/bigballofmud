package io.pillopl.bigballofmud.dtos;

import java.util.UUID;


public class BookRequest {
    private UUID bookId;
    private UUID holderId;
    private Integer days;
    private Boolean openEndedHold;

    public BookRequest(UUID bookId, UUID holderId, Integer days, Boolean openEndedHold) {
        this.bookId = bookId;
        this.holderId = holderId;
        this.days = days;
        this.openEndedHold = openEndedHold;
    }

    public BookRequest() {
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public UUID getHolderId() {
        return holderId;
    }

    public void setHolderId(UUID holderId) {
        this.holderId = holderId;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public boolean isOpenEndedHold() {
        return openEndedHold;
    }

    public void setOpenEndedHold(boolean openEndedHold) {
        this.openEndedHold = openEndedHold;
    }
}
