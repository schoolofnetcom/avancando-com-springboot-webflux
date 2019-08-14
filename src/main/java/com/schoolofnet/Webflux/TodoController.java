package com.schoolofnet.Webflux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
@RequestMapping("/todos")
public class TodoController {

	@Autowired
	private TodoRepository todoRepository;
	
	@Autowired
	private TransactionTemplate transactionTemplate;
	
	@Autowired
	@Qualifier("jdbcScheduler")
	private Scheduler jdbcSchduler;
	
	public TodoController(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}
	
	@GetMapping
	@ResponseBody
	public Flux<Todo> findAll() {
		return Flux.defer(() -> Flux.fromIterable(this.todoRepository.findAll())).subscribeOn(jdbcSchduler);
	}
	
	@GetMapping("/{id}")
	@ResponseBody
	public Mono<Todo> findById(@PathVariable("id") Long id) {
		return Mono.justOrEmpty(this.todoRepository.findById(id));
	}
	
	@PostMapping
	public Mono<Todo> save(@RequestBody Todo todo) {
		Mono op = Mono.fromCallable(() -> this.transactionTemplate.execute(action -> {
			Todo newTodo = this.todoRepository.save(todo);
			return newTodo;
		}));
	
		return op; 
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> remove(@PathVariable("id") Long id) {
		return Mono.fromCallable(() -> this.transactionTemplate.execute(action -> {
			this.todoRepository.deleteById(id);
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		})).subscribeOn(jdbcSchduler);
	}
}
