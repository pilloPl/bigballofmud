package io.pillopl.newmodel.catalogue;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(of = "bookIsbn")
@AllArgsConstructor
class Book {

    @NonNull
    private ISBN bookIsbn;
    @NonNull
    private Title title;
    @NonNull
    private Author author;

}


@Value
class Title {

    @NonNull String title;

    Title(String title) {
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Title cannot be longer than 100");
        }
        this.title = title.trim();
    }

}

@Value
class Author {

    @NonNull String name;

    Author(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        if (name.length() > 60) {
            throw new IllegalArgumentException("Author cannot be longer than 60");
        }
        this.name = name.trim();
    }
}