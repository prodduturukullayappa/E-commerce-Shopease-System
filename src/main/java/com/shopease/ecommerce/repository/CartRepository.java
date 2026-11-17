package com.shopease.ecommerce.repository;

import com.shopease.ecommerce.model.CartItem;
import com.shopease.ecommerce.model.Product;
import com.shopease.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserOrderByIdDesc(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    void deleteByUser(User user);
}
