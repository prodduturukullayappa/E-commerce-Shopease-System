package com.shopease.ecommerce.repository;

import com.shopease.ecommerce.model.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findTop8ByFeaturedTrueOrderByCreatedAtDesc();

    List<Product> findTop8ByOrderByCreatedAtDesc();

    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    List<Product> searchAndFilter(@Param("keyword") String keyword,
                                  @Param("categoryId") Long categoryId,
                                  Sort sort);
}
