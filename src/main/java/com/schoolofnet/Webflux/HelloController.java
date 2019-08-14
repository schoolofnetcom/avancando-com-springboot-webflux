package com.schoolofnet.Webflux;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class HelloController {

//	@GetMapping
//	@ResponseBody
//	public Publisher<String> sayHello() {
//		return Mono.just("Hello World from Spring Webflux by School of net");
//	}
	
	public Mono<ServerResponse> sayHelloFlux(ServerRequest req) {
//		return Mono.just("Hello World from Spring Webflux by School of net");
		return ServerResponse.ok().syncBody("Hello from webflux RouterFunction");
	}	
}
