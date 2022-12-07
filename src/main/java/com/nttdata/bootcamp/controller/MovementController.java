package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Movement;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.MovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/movement")
public class MovementController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MovementController.class);
	@Autowired
	private MovementService movementService;


	//Transfer search
	@GetMapping("/findAllMovements")
	public Flux<Movement> findAllMovements() {
		Flux<Movement> movementsFlux = movementService.findAll();
		LOGGER.info("Registered movements: " + movementsFlux);
		return movementsFlux;
	}

	//Transfer by AccountNumber
	@GetMapping("/findAllMovementsByNumber/{accountNumber}")
	public Flux<Movement> findAllMovementsByNumber(@PathVariable("accountNumber") String accountNumber) {
		Flux<Movement> movementsFlux = movementService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered movements of account number: "+accountNumber +"-" + movementsFlux);
		return movementsFlux;
	}

	//Transfer  by transactionNumber
	@CircuitBreaker(name = "movement", fallbackMethod = "fallBackGetMovement")
	@GetMapping("/findByMovementNumber/{numberMovement}")
	public Mono<Movement> findByMovementNumber(@PathVariable("numberMovement") String numberMovement) {
		LOGGER.info("Searching Movement by number: " + numberMovement);
		return movementService.findByNumber(numberMovement);
	}

	//Save Transfer
	@CircuitBreaker(name = "movement", fallbackMethod = "fallBackGetMovement")
	@PostMapping(value = "/saveMovement/{typeMovement}")
	public Mono<Movement> saveMovement(@RequestBody Movement dataMovement, @PathVariable("typeMovement") String typeMovement){
		Mono.just(dataMovement).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());
					t.setTypeMovement(typeMovement);

				}).onErrorReturn(dataMovement).onErrorResume(e -> Mono.just(dataMovement))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Movement> movementsMono = movementService.saveMovement(dataMovement);
		return movementsMono;
	}

	@PostMapping(value = "/saveTransactionOrigin")
	public Mono<Movement> saveTransactionOrigin(@RequestBody Movement dataMovement){
		Mono.just(dataMovement).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());
					t.setTypeMovement("Transfer");

				}).onErrorReturn(dataMovement).onErrorResume(e -> Mono.just(dataMovement))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Movement> movementsMono = movementService.saveMovement(dataMovement);
		return movementsMono;
	}

	@PostMapping(value = "/saveTransactionDestination")
	public Mono<Movement> saveTransactionDestination(@RequestBody Movement dataMovement){
		Mono.just(dataMovement).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());
					t.setTypeMovement("Transfer");

				}).onErrorReturn(dataMovement).onErrorResume(e -> Mono.just(dataMovement))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Movement> movementsMono = movementService.saveMovement(dataMovement);
		return movementsMono;
	}



	//Update Transfer
	@CircuitBreaker(name = "movement", fallbackMethod = "fallBackGetMovement")
	@PutMapping("/updateMovements/{numberMovement}")
	public Mono<Movement> updateMovements(@PathVariable("numberTransfer") String numberMovements,
										  @Valid @RequestBody Movement dataMovement) {
		Mono.just(dataMovement).doOnNext(t -> {

					t.setMovementNumber(numberMovements);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataMovement).onErrorResume(e -> Mono.just(dataMovement))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Movement> updateTransfer = movementService.updateMovement(dataMovement);
		return updateTransfer;
	}


	//Delete Transfer
	@CircuitBreaker(name = "movement", fallbackMethod = "fallBackGetMovement")
	@DeleteMapping("/deleteMovement/{numberMovement}")
	public Mono<Void> deleteMovement(@PathVariable("numberTransaction") String numberMovement) {
		LOGGER.info("Deleting Movement by number: " + numberMovement);
		Mono<Void> delete = movementService.deleteMovement(numberMovement);
		return delete;

	}

	private Mono<Movement> fallBackGetMovement(Exception e){
		Movement movement = new Movement();
		Mono<Movement> movementsMono= Mono.just(movement);
		return movementsMono;
	}




}
