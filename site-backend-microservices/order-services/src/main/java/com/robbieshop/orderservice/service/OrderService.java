package com.robbieshop.orderservice.service;

import com.robbieshop.orderservice.dto.InventoryResponseDTO;
import com.robbieshop.orderservice.dto.OrderItemsDTO;
import com.robbieshop.orderservice.dto.OrderRequestDTO;
import com.robbieshop.orderservice.model.Order;
import com.robbieshop.orderservice.model.OrderItems;
import com.robbieshop.orderservice.repository.OrderRespository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRespository orderRespository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    public String placeOrder(OrderRequestDTO orderRequesDTO){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderItems> orderItemsList = orderRequesDTO.getOrderItemsDTOs().stream()
                                                .map(orderItemsDTO -> orderItemsBuilder(orderItemsDTO))
                                                .toList();

        order.setOrderItemsList(orderItemsList);

        List<String> skuCodes = order.getOrderItemsList().stream()
                                .map(orderItems -> orderItems.getSkuCode())
                                .toList();

        Span inventoryService = tracer.nextSpan().name("inventory-service");
        try(Tracer.SpanInScope isSpanInScope = tracer.withSpan(inventoryService.start())){
            InventoryResponseDTO[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponseDTO[].class)
                    .block();

            boolean allInStock = Arrays.stream(inventoryResponseArray).allMatch(inventoryResponse -> inventoryResponse.getIsStock());

            if(allInStock){
                orderRespository.save(order);
                return "Order placed, thank you!";
            } else {
                throw new IllegalArgumentException("Product insufficient, place make another order");
            }
        } finally {
            inventoryService.end();
        }

    }

    public OrderItems orderItemsBuilder(OrderItemsDTO orderItemsDTO){
        OrderItems orderItems = OrderItems.builder()
                .price(orderItemsDTO.getPrice())
                .quantity(orderItemsDTO.getQuantity())
                .skuCode(orderItemsDTO.getSkuCode())
                .build();

        return orderItems;
    }

}
