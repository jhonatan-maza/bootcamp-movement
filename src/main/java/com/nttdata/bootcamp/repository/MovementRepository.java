package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.Movements;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

//Mongodb Repository
public interface MovementRepository extends ReactiveCrudRepository<Movements, String> {
}
