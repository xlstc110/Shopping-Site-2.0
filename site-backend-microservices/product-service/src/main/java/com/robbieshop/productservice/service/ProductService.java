package com.robbieshop.productservice.service;

import com.robbieshop.productservice.dto.ProductRequestDTO;
import com.robbieshop.productservice.dto.ProductResponseDTO;
import com.robbieshop.productservice.model.Product;
import com.robbieshop.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequestDTO productRequestDTO){
        Product product = Product.builder()
                        .name(productRequestDTO.getName())
                        .description(productRequestDTO.getDescription())
                        .price(productRequestDTO.getPrice())
                        .build();

        productRepository.save(product);
        log.info("product saved: {}", product.getId());
    }

    public List<ProductResponseDTO> getAllProducts(){

        List<Product> products = productRepository.findAll();

        List<ProductResponseDTO> productResponseDTOs = products.stream().map(product -> productResponseDTOBuilder(product)).toList();

        return productResponseDTOs;
    }

    public ProductResponseDTO productResponseDTOBuilder(Product product){
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }
}
