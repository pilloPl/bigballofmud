package io.pillopl.bigballofmud.repositories;

import io.pillopl.bigballofmud.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {

}
