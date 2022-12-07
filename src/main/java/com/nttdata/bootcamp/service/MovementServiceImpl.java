package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Movement;
import com.nttdata.bootcamp.repository.MovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class MovementServiceImpl implements MovementService {
    @Autowired
    private MovementRepository movementRepository;

    @Override
    public Flux<Movement> findAll() {
        Flux<Movement> movementsFlux = movementRepository.findAll();
        return movementsFlux;
    }

    @Override
    public Flux<Movement> findByAccountNumber(String accountNumber) {
        Flux<Movement> movementsFlux = movementRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber));
        return movementsFlux;
    }

    @Override
    public Mono<Movement> findByNumber(String Number) {
        Mono<Movement> movementsMono = movementRepository
                .findAll()
                .filter(x -> x.getMovementNumber().equals(Number))
                .next();
        return movementsMono;
    }

    public Mono<Movement> saveMovement(Movement dataMovement) {
        dataMovement.setStatus("active");
        return movementRepository.save(dataMovement);

    }

    @Override
    public Mono<Movement> updateMovement(Movement dataMovement) {

        Mono<Movement> transactionMono = findByNumber(dataMovement.getMovementNumber());
        try {
            dataMovement.setDni(transactionMono.block().getDni());
            dataMovement.setAmount(transactionMono.block().getAmount());
            dataMovement.setAccountNumber(transactionMono.block().getAccountNumber());
            dataMovement.setMovementNumber(transactionMono.block().getMovementNumber());
            dataMovement.setTypeMovement(transactionMono.block().getTypeMovement());
            dataMovement.setStatus(transactionMono.block().getStatus());
            dataMovement.setCreationDate(transactionMono.block().getCreationDate());
            return movementRepository.save(dataMovement);
        }catch (Exception e){
            return Mono.<Movement>error(new Error("The movement " + dataMovement.getMovementNumber() + " do not exists"));
        }
    }

    @Override
    public Mono<Void> deleteMovement(String Number) {
        Mono<Movement> movementsMono = findByNumber(Number);
        try {
            Movement movement = movementsMono.block();
            return movementRepository.delete(movement);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("The movement number" + Number+ " do not exists"));
        }
    }


}
