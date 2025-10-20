package com.krillinator.demo_5;

import com.krillinator.demo_5.product.Product;
import com.krillinator.demo_5.product.ProductRepository;
import com.krillinator.demo_5.product.dto.ProductResponseDTO;
import com.krillinator.demo_5.product.dto.ProductValidatorDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureWebTestClient
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void clearDatabase() {
        productRepository.deleteAll().block();
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        // Arrange
        ProductValidatorDTO dto = new ProductValidatorDTO(
                "Mango", "Sweet tropical fruit",
                BigDecimal.valueOf(25.10), false
        );

        ProductResponseDTO created = webTestClient.post()
                .uri("/api/v1/product/create")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponseDTO.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.id());
        Long id = created.id();

        Product beforeDelete = productRepository.findById(id).block();
        System.out.println("Before delete = " + beforeDelete);

        // Act & Assert
        webTestClient.delete()
                .uri("/api/v1/product/delete/{id}", id)
                .exchange()
                .expectStatus().isNoContent();
    }
}
