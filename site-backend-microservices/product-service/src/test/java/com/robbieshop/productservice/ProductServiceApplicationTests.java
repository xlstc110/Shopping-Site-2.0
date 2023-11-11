package com.robbieshop.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robbieshop.productservice.dto.ProductRequestDTO;
import com.robbieshop.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers//JUnit 5 now knows we are using test containers to run this test
@AutoConfigureMockMvc//To configure MockMvc as a dependency
class ProductServiceApplicationTests {

	@Container//Junit 5 now knows this is a container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer();
	@Autowired//Dependent injection for MockMVC
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;//This object can convert POJO object to JSON, or JSON object to POJO
	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		//set up dynamic properties for testing with a MongoDB container. @DynamicPropertySource is used in Spring to
		//dynamically register properties in the Environment, and it's often used in integration tests to override properties based on what's present in the test environment.

		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);//or regular lambda: () -> mongoDBContainer.getReplicaSetUrl()
		//This mongoDBContainer::getReplicaSetUrl is a method reference in Java, which is a shorthand notation for a lambda expression that calls a method.
		//Here, it's presumably calling the getReplicaSetUrl method on an instance of a mongoDBContainer class, which should return the URI needed to connect to the MongoDB instance managed by the test container.

		//When your tests run, Spring will call this method to get the actual MongoDB URI from the running container and will set it as the property value,
		//so your data layer can connect to the correct MongoDB instance. This approach is very helpful for ensuring that your tests are not dependent on a specific configuration
		//and can adapt to the environment where they are run, especially when dealing with containers.
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequestDTO productRequestDTO = getProductRequestDTO();
		String productRequestJson = objectMapper.writeValueAsString(productRequestDTO);

		//The MockMvcRequestBuilders can mock a client request in different request type, get, post, update, delete
		//1. The mock will make a call to the API /api/product with a Post request type, and expect the status is Created.
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestJson))
				.andExpect(status().isCreated());
		//sample respond: Prodcut 64ed82d94307cf3f1673326a is saved

		//Validate again that the database successfully added the product:
		Assertions.assertTrue(productRepository.findAll().size() > 0);
	}

	@Test
	void shouldGetAllProducts() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	private ProductRequestDTO getProductRequestDTO() {
		return ProductRequestDTO.builder()
				.name("Iphone 15")
				.description("Iphone 15, 2023")
				.price(1299.99)
				.build();
	}

}
