package io.pillopl.bigballofmud.repositories;

import io.pillopl.bigballofmud.entities.BookHolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookHolderRepository extends JpaRepository<BookHolderEntity, UUID> {

}
