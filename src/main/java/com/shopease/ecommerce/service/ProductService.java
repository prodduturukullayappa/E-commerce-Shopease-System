package com.shopease.ecommerce.service;

import com.shopease.ecommerce.model.Category;
import com.shopease.ecommerce.model.Product;
import com.shopease.ecommerce.repository.CategoryRepository;
import com.shopease.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findTop8ByFeaturedTrueOrderByCreatedAtDesc();
    }

    public List<Product> getLatestProducts() {
        return productRepository.findTop8ByOrderByCreatedAtDesc();
    }

    public List<Product> searchAndFilterProducts(String keyword, Long categoryId, String sortBy) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        if ("price_asc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        }
        return productRepository.searchAndFilter(cleanKeyword, categoryId, sort);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
    }

    public Product saveProduct(Product product, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Selected category does not exist."));
            product.setCategory(category);
        }
        if (product.getImageUrl() == null || product.getImageUrl().trim().isEmpty()) {
            product.setImageUrl("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&q=80");
        }
        return productRepository.save(product);
    }

    public void updateStock(Long productId, Integer stock) {
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        Product product = getProductById(productId);
        product.setStock(stock);
        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found.");
        }
        productRepository.deleteById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category saveCategory(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required.");
        }
        if (categoryRepository.findByNameIgnoreCase(name.trim()).isPresent()) {
            throw new IllegalArgumentException("Category already exists.");
        }
        return categoryRepository.save(new Category(name.trim(), description != null ? description.trim() : ""));
    }
}
