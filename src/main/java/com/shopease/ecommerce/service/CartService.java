package com.shopease.ecommerce.service;

import com.shopease.ecommerce.model.CartItem;
import com.shopease.ecommerce.model.Product;
import com.shopease.ecommerce.model.User;
import com.shopease.ecommerce.repository.CartRepository;
import com.shopease.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartItems(User user) {
        return cartRepository.findByUserOrderByIdDesc(user);
    }

    public int getCartCount(User user) {
        return cartRepository.findByUserOrderByIdDesc(user)
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public BigDecimal calculateTotal(List<CartItem> items) {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));

        if (product.getStock() <= 0) {
            throw new IllegalArgumentException("This product is currently out of stock.");
        }

        Optional<CartItem> existingOpt = cartRepository.findByUserAndProduct(user, product);
        if (existingOpt.isPresent()) {
            CartItem existing = existingOpt.get();
            int updatedQty = existing.getQuantity() + quantity;
            if (updatedQty > product.getStock()) {
                throw new IllegalArgumentException("Only " + product.getStock() + " units available in stock.");
            }
            existing.setQuantity(updatedQty);
            cartRepository.save(existing);
        } else {
            if (quantity > product.getStock()) {
                throw new IllegalArgumentException("Only " + product.getStock() + " units available in stock.");
            }
            cartRepository.save(new CartItem(user, product, quantity));
        }
    }

    @Transactional
    public void updateQuantity(User user, Long cartItemId, int delta) {
        CartItem item = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found."));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized cart access.");
        }

        int newQuantity = item.getQuantity() + delta;
        if (newQuantity <= 0) {
            cartRepository.delete(item);
        } else {
            if (newQuantity > item.getProduct().getStock()) {
                throw new IllegalArgumentException("Cannot exceed available stock (" + item.getProduct().getStock() + ").");
            }
            item.setQuantity(newQuantity);
            cartRepository.save(item);
        }
    }

    @Transactional
    public void removeItem(User user, Long cartItemId) {
        CartItem item = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found."));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized cart access.");
        }
        cartRepository.delete(item);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }
}
