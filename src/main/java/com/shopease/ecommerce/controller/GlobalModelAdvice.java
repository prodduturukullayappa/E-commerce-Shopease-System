package com.shopease.ecommerce.controller;

import com.shopease.ecommerce.repository.UserRepository;
import com.shopease.ecommerce.service.CartService;
import com.shopease.ecommerce.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UserRepository userRepository;
    private final CartService cartService;
    private final ProductService productService;

    public GlobalModelAdvice(UserRepository userRepository,
                             CartService cartService,
                             ProductService productService) {
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.productService = productService;
    }

    @ModelAttribute
    public void populateGlobalAttributes(Authentication authentication, Model model) {
        model.addAttribute("navCategories", productService.getAllCategories());
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            userRepository.findByEmail(authentication.getName()).ifPresent(user -> {
                model.addAttribute("loggedInUser", user);
                if ("ROLE_USER".equals(user.getRole())) {
                    model.addAttribute("cartCount", cartService.getCartCount(user));
                } else {
                    model.addAttribute("cartCount", 0);
                }
            });
        } else {
            model.addAttribute("cartCount", 0);
        }
    }
}
