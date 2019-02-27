package io.pillopl.bigballofmud.services;


import io.pillopl.bigballofmud.entities.BookHolderEntity;
import io.pillopl.bigballofmud.entities.HolderRentalFeeEntity;
import io.pillopl.bigballofmud.repositories.HolderRentalFeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class HolderRentalFeeService {

    private final BookHolderService bookHolderService;
    private final HolderRentalFeeRepository rentalFeeRepository;

    public HolderRentalFeeService(BookHolderService bookHolderService, HolderRentalFeeRepository rentalFeeRepository) {
        this.bookHolderService = bookHolderService;
        this.rentalFeeRepository = rentalFeeRepository;
    }

    public BigDecimal currentFee(UUID holderId) {
        BookHolderEntity holder = bookHolderService.findHolder(holderId);
        HolderRentalFeeEntity fee = rentalFeeRepository.findByBookHolderEntity(holder);
        if(fee == null) {
            return BigDecimal.ZERO;
        }
        return fee.getFee();
    }
}
