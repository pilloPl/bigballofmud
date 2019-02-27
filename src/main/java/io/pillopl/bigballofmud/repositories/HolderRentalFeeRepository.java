package io.pillopl.bigballofmud.repositories;

import io.pillopl.bigballofmud.entities.BookHolderEntity;
import io.pillopl.bigballofmud.entities.HolderRentalFeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HolderRentalFeeRepository extends JpaRepository<HolderRentalFeeEntity, UUID> {

    HolderRentalFeeEntity findByBookHolderEntity(BookHolderEntity entity);

}
