package io.pillopl.bigballofmud.entities;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.UUID;


@Entity
public class HolderRentalFeeEntity {

    @Id @GeneratedValue
    private UUID id;

    private BigDecimal fee = BigDecimal.ZERO;

    @OneToOne
    private BookHolderEntity bookHolderEntity;

    public BigDecimal getFee() {
        return this.fee;
    }

    public BookHolderEntity getBookHolderEntity() {
        return this.bookHolderEntity;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public void setBookHolderEntity(BookHolderEntity bookHolderEntity) {
        this.bookHolderEntity = bookHolderEntity;
    }
}
