package com.robbieshop.productservice.controller;

import com.robbieshop.productservice.dto.ProductRequestDTO;
import com.robbieshop.productservice.dto.ProductResponseDTO;
import com.robbieshop.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequestDTO productRequestDTO){

        productService.createProduct(productRequestDTO);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponseDTO> getAllProducts(){

        List<ProductResponseDTO> products = productService.getAllProducts();

        return products;
    }

}
