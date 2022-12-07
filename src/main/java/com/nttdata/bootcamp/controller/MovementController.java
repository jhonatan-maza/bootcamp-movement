package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Movements;
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
	@GetMapping("/")
	public Flux<Movements> findAllMovements() {
		Flux<Movements> movementsFlux = movementService.findAll();
		LOGGER.info("Registered movements: " + movementsFlux);
		return movementsFlux;
	}

	//Transfer by AccountNumber
	@GetMapping("/findAllMovementsByNumber/{accountNumber}")
	public Flux<Movements> findAllMovementsByNumber(@PathVariable("accountNumber") String accountNumber) {
		Flux<Movements> movementsFlux = movementService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered movements of account number: "+accountNumber +"-" + movementsFlux);
		return movementsFlux;
	}

	//Transfer  by transactionNumber
	@CircuitBreaker(name = "movement", fallbackMethod = "fallBackGetMovement")
	@GetMapping("/findByMovementNumber/{numberTransfer}")
	public Mono<Movements> findByMovementNumber(@PathVariable("numberMovement") String numberMovement) {
		LOGGER.info("Searching Movement by number: " + numberMovement);
		return movementService.findByNumber(numberMovement);
	}

	//Save Transfer
	@CircuitBreaker(name = "movement", fallbackMethod = "fallBackGetMovement")
	@PostMapping(value = "/saveMovement")
	public Mono<Movements> saveMovement(@RequestBody Movements dataMovements, @PathVariable("typeMovement") String typeMovement){
		Mono.just(dataMovements).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataMovements).onErrorResume(e -> Mono.just(dataMovements))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Movements> movementsMono = movementService.saveMovement(dataMovements);
		return movementsMono;
	}

	@PostMapping(value = "/saveTransactionOrigin")
	public Mono<Movements> saveTransactionOrigin(@RequestBody Movements dataMovements){
		Mono.just(dataMovements).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());
					t.setTypeMovement("Transfer");

				}).onErrorReturn(dataMovements).onErrorResume(e -> Mono.just(dataMovements))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Movements> movementsMono = movementService.saveMovement(dataMovements);
		return movementsMono;
	}

	@PostMapping(value = "/saveTransactionDestination")
	public Mono<Movements> saveTransactionDestination(@RequestBody Movements dataMovements){
		Mono.just(dataMovements).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());
					t.setTypeMovement("Transfer");

				}).onErrorReturn(dataMovements).onErrorResume(e -> Mono.just(dataMovements))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Movements> movementsMono = movementService.saveMovement(dataMovements);
		return movementsMono;
	}



	//Update Transfer
	@CircuitBreaker(name = "movement", fallbackMethod = "fallBackGetMovement")
	@PutMapping("/updateMovements/{numberMovement}")
	public Mono<Movements> updateMovements(@PathVariable("numberTransfer") String numberMovements,
										  @Valid @RequestBody Movements dataMovements) {
		Mono.just(dataMovements).doOnNext(t -> {

					t.setMovementNumber(numberMovements);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataMovements).onErrorResume(e -> Mono.just(dataMovements))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Movements> updateTransfer = movementService.updateMovement(dataMovements);
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

	private Mono<Movements> fallBackGetMovement(Exception e){
		Movements movements = new Movements();
		Mono<Movements> movementsMono= Mono.just(movements);
		return movementsMono;
	}




}
