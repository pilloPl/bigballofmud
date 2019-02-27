package io.pillopl.bigballofmud.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;
import java.util.UUID;

@Entity
public class BookHolderEntity {

    @Id @GeneratedValue
    private UUID id;

    public enum HolderType {
        Regular, Researcher
    }

    private String holderName;

    private HolderType type;

    private String email;

    @OneToMany
    private Set<BookEntity> books;

    public UUID getId() {
        return this.id;
    }

    public String getHolderName() {
        return this.holderName;
    }

    public HolderType getType() {
        return this.type;
    }

    public Set<BookEntity> getBooks() {
        return this.books;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public void setType(HolderType type) {
        this.type = type;
    }

    public void setBooks(Set<BookEntity> books) {
        this.books = books;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



}
