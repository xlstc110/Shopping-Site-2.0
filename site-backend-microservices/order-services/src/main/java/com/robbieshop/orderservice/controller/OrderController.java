package com.robbieshop.orderservice.controller;

import com.robbieshop.orderservice.dto.OrderRequestDTO;
import com.robbieshop.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallBackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequestDTO orderRequestDTO){

        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequestDTO));
    }

    public CompletableFuture<String> fallBackMethod(OrderRequestDTO orderRequestDTO, RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(() -> "Something went wrong, please place your order next time!");
    }
}
