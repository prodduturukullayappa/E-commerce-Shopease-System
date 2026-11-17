package com.shopease.ecommerce;

import com.shopease.ecommerce.model.Category;
import com.shopease.ecommerce.model.User;
import com.shopease.ecommerce.repository.CategoryRepository;
import com.shopease.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   CategoryRepository categoryRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@shopease.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("Store Administrator");
                admin.setEmail("admin@shopease.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setPhone("9876543210");
                admin.setAddress("Admin HQ, MG Road, Bengaluru");
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
            }

            if (categoryRepository.count() == 0) {
                categoryRepository.saveAll(List.of(
                        new Category("Electronics", "Smartphones, laptops, audio & gadgets"),
                        new Category("Fashion", "Men's & Women's apparel, footwear & accessories"),
                        new Category("Home & Kitchen", "Appliances, decor, cookware & essentials"),
                        new Category("Books & Stationery", "Bestsellers, academic books & office supplies"),
                        new Category("Sports & Fitness", "Gym gear, outdoor equipment & sportswear")
                ));
            }
        };
    }
}
