package com.deny.inventoryservice;

import com.deny.inventoryservice.model.Inventory;
import com.deny.inventoryservice.repository.InventoryRepository;
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
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return arg -> {
            if (inventoryRepository.findAll().isEmpty()) {
                var inventory = new Inventory();
                inventory.setQuantity(10);
                inventory.setSkuCode("pixel_4a_5g");
                var inventory1 = new Inventory();
                inventory1.setQuantity(0);
                inventory1.setSkuCode("pixel_6a");

                inventoryRepository.save(inventory);
                inventoryRepository.save(inventory1);
            }
        };
    }
}
