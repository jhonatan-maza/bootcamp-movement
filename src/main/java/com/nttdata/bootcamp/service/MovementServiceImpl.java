package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Movements;
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
    public Flux<Movements> findAll() {
        Flux<Movements> movementsFlux = movementRepository.findAll();
        return movementsFlux;
    }

    @Override
    public Flux<Movements> findByAccountNumber(String accountNumber) {
        Flux<Movements> movementsFlux = movementRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber));
        return movementsFlux;
    }

    @Override
    public Mono<Movements> findByNumber(String Number) {
        Mono<Movements> movementsMono = movementRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(Number))
                .next();
        return movementsMono;
    }

    public Mono<Movements> saveMovement(Movements dataMovements) {
        dataMovements.setStatus("active");
        return movementRepository.save(dataMovements);

    }

    @Override
    public Mono<Movements> updateMovement(Movements dataMovements) {

        Mono<Movements> transactionMono = findByNumber(dataMovements.getMovementNumber());
        try {
            dataMovements.setDni(transactionMono.block().getDni());
            dataMovements.setAmount(transactionMono.block().getAmount());
            dataMovements.setAccountNumber(transactionMono.block().getAccountNumber());
            dataMovements.setMovementNumber(transactionMono.block().getMovementNumber());
            dataMovements.setTypeMovements(transactionMono.block().getTypeMovements());
            dataMovements.setStatus(transactionMono.block().getStatus());
            dataMovements.setCreationDate(transactionMono.block().getCreationDate());
            return movementRepository.save(dataMovements);
        }catch (Exception e){
            return Mono.<Movements>error(new Error("The movement " + dataMovements.getMovementNumber() + " do not exists"));
        }
    }

    @Override
    public Mono<Void> deleteMovement(String Number) {
        Mono<Movements> movementsMono = findByNumber(Number);
        try {
            Movements movements = movementsMono.block();
            return movementRepository.delete(movements);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("The movement number" + Number+ " do not exists"));
        }
    }


}
