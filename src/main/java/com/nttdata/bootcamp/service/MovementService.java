package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Movements;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface MovementService {

    public Flux<Movements> findAll();
    public Flux<Movements> findByAccountNumber(String accountNumber);

    public Mono<Movements> findByNumber(String number);
    public Mono<Movements> saveMovement(Movements movements);
    public Mono<Movements> updateMovement(Movements movements);
    public Mono<Void> deleteMovement(String accountNumber);




}
