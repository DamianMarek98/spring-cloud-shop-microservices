package com.deny.productservice.service;

import com.deny.productservice.dto.ProductDto;
import com.deny.productservice.dto.ProductRequest;
import com.deny.productservice.model.Product;
import com.deny.productservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(ProductRequest productRequest) {
        var product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price()).
                build();

        productRepository.save(product);
        log.info("new product with id: {} is saved", product.getId());
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(mapToProductDto())
                .collect(Collectors.toList());
    }

    private static Function<Product, ProductDto> mapToProductDto() {
        return product -> new ProductDto(product.getId(), product.getName(), product.getDescription(),
                product.getPrice());
    }
}
