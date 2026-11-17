package com.shopease.ecommerce.controller;

import com.shopease.ecommerce.model.Order;
import com.shopease.ecommerce.model.Product;
import com.shopease.ecommerce.service.OrderService;
import com.shopease.ecommerce.service.ProductService;
import com.shopease.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    public AdminController(ProductService productService,
                           OrderService orderService,
                           UserService userService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) Long editId, Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("customers", userService.getAllCustomers());
        model.addAttribute("totalSales", orderService.getTotalSales());
        model.addAttribute("totalOrders", orderService.getTotalOrdersCount());
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalCustomers", userService.getAllCustomers().size());

        if (editId != null) {
            model.addAttribute("productForm", productService.getProductById(editId));
            model.addAttribute("isEditing", true);
        } else {
            model.addAttribute("productForm", new Product());
            model.addAttribute("isEditing", false);
        }
        return "admin-dashboard";
    }

    @GetMapping("/products/{id}")
    public String adminProductDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            return "admin-product-details";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    @GetMapping("/orders/{id}")
    public String adminOrderDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getOrderById(id);
            model.addAttribute("order", order);
            return "admin-order-details";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute("productForm") Product product,
                              BindingResult bindingResult,
                              @RequestParam Long categoryId,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fill in all required product fields correctly.");
            return "redirect:/admin/dashboard";
        }
        try {
            productService.saveProduct(product, categoryId);
            redirectAttributes.addFlashAttribute("successMessage",
                    product.getId() != null ? "Product updated successfully." : "New product added to store.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/products/stock")
    public String updateStock(@RequestParam Long productId,
                              @RequestParam Integer stock,
                              RedirectAttributes redirectAttributes) {
        try {
            productService.updateStock(productId, stock);
            redirectAttributes.addFlashAttribute("successMessage", "Stock updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/products/delete")
    public String deleteProduct(@RequestParam Long productId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(productId);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot delete product (it may be linked to existing orders). Set stock to 0 instead.");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@RequestParam String name,
                               @RequestParam(required = false) String description,
                               RedirectAttributes redirectAttributes) {
        try {
            productService.saveCategory(name, description);
            redirectAttributes.addFlashAttribute("successMessage", "Category added successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/orders/status")
    public String updateOrderStatus(@RequestParam Long orderId,
                                    @RequestParam String status,
                                    @RequestParam(required = false, defaultValue = "dashboard") String returnTo,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(orderId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated to " + status + ".");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        if ("details".equalsIgnoreCase(returnTo)) {
            return "redirect:/admin/orders/" + orderId;
        }
        return "redirect:/admin/dashboard";
    }
}
