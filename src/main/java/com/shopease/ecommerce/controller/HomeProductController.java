package com.shopease.ecommerce.controller;

import com.shopease.ecommerce.model.Product;
import com.shopease.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeProductController {

    private final ProductService productService;

    public HomeProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("latestProducts", productService.getLatestProducts());
        return "index";
    }

    @GetMapping("/products")
    public String listProducts(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false, defaultValue = "newest") String sort,
                               Model model) {
        List<Product> products = productService.searchAndFilterProducts(keyword, categoryId, sort);
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("selectedKeyword", keyword != null ? keyword : "");
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedSort", sort);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            return "product-details";
        } catch (IllegalArgumentException ex) {
            return "redirect:/products";
        }
    }
}
