package com.robbieshop.inventoryservice;

import com.robbieshop.inventoryservice.model.Inventory;
import com.robbieshop.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository){
		return args -> {
			//populate an inventory with 30 quantity
			Inventory inventory = new Inventory().builder()
					.skuCode("iphone 15")
					.quantity(5000)
					.build();

			//populate an inventory with 0 quantity
			Inventory inventory0 = new Inventory().builder()
					.skuCode("iphone 14")
					.quantity(0)
					.build();

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory0);
		};
	}
}
