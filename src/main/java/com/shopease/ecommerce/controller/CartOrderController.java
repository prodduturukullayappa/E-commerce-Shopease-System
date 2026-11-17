package com.shopease.ecommerce.controller;

import com.shopease.ecommerce.model.CartItem;
import com.shopease.ecommerce.model.Order;
import com.shopease.ecommerce.model.Product;
import com.shopease.ecommerce.model.User;
import com.shopease.ecommerce.service.CartService;
import com.shopease.ecommerce.service.OrderService;
import com.shopease.ecommerce.service.ProductService;
import com.shopease.ecommerce.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CartOrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;

    public CartOrderController(CartService cartService,
                               OrderService orderService,
                               UserService userService,
                               ProductService productService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/cart")
    public String viewCart(Authentication authentication, Model model) {
        User user = userService.findByEmail(authentication.getName());
        List<CartItem> items = cartService.getCartItems(user);
        BigDecimal total = cartService.calculateTotal(items);

        model.addAttribute("cartItems", items);
        model.addAttribute("cartTotal", total);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(Authentication authentication,
                            @RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(authentication.getName());
        try {
            cartService.addToCart(user, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Product added to your shopping cart.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateQuantity(Authentication authentication,
                                 @RequestParam Long itemId,
                                 @RequestParam int delta,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(authentication.getName());
        try {
            cartService.updateQuantity(user, itemId, delta);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeItem(Authentication authentication,
                             @RequestParam Long itemId,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(authentication.getName());
        try {
            cartService.removeItem(user, itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    /**
     * Direct "Buy Now" Checkout Page for a single product
     */
    @GetMapping("/checkout/buy-now")
    public String buyNowCheckout(Authentication authentication,
                                 @RequestParam Long productId,
                                 @RequestParam(defaultValue = "1") int quantity,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(authentication.getName());
        try {
            Product product = productService.getProductById(productId);
            if (product.getStock() < quantity || quantity < 1) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected quantity is not available in stock.");
                return "redirect:/products/" + productId;
            }
            BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            model.addAttribute("user", user);
            model.addAttribute("isBuyNow", true);
            model.addAttribute("buyNowProduct", product);
            model.addAttribute("buyNowQuantity", quantity);
            model.addAttribute("cartTotal", total);
            return "checkout";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/products";
        }
    }

    /**
     * Standard Cart Checkout Page
     */
    @GetMapping("/checkout")
    public String checkoutPage(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(authentication.getName());
        List<CartItem> items = cartService.getCartItems(user);
        if (items.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Add items to your cart before checking out.");
            return "redirect:/cart";
        }
        model.addAttribute("user", user);
        model.addAttribute("isBuyNow", false);
        model.addAttribute("cartItems", items);
        model.addAttribute("cartTotal", cartService.calculateTotal(items));
        return "checkout";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(Authentication authentication,
                             @RequestParam(defaultValue = "false") boolean isBuyNow,
                             @RequestParam(required = false) Long productId,
                             @RequestParam(defaultValue = "1") int quantity,
                             @RequestParam String shippingAddress,
                             @RequestParam String phone,
                             @RequestParam(defaultValue = "Cash on Delivery") String paymentMethod,
                             @RequestParam(required = false) String paymentReference,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(authentication.getName());
        try {
            Order order;
            if (isBuyNow && productId != null) {
                order = orderService.placeBuyNowOrder(
                        user, productId, quantity, shippingAddress, phone, paymentMethod, paymentReference);
            } else {
                order = orderService.placeOrder(
                        user, shippingAddress, phone, paymentMethod, paymentReference);
            }
            redirectAttributes.addFlashAttribute("successMessage",
                    "Order placed successfully! Order ID: " + order.getOrderNumber() +
                    " | Payment: " + order.getPaymentMethod() + " (" + order.getPaymentStatus() + ")");
            return "redirect:/orders";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            if (isBuyNow && productId != null) {
                return "redirect:/checkout/buy-now?productId=" + productId + "&quantity=" + quantity;
            }
            return "redirect:/checkout";
        }
    }

    @GetMapping("/orders")
    public String myOrders(Authentication authentication, Model model) {
        User user = userService.findByEmail(authentication.getName());
        model.addAttribute("orders", orderService.getUserOrders(user));
        return "my-orders";
    }

    @PostMapping("/orders/cancel")
    public String cancelOrder(Authentication authentication,
                              @RequestParam Long orderId,
                              RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(authentication.getName());
        try {
            orderService.cancelOrder(user, orderId);
            redirectAttributes.addFlashAttribute("successMessage", "Order cancelled and stock restored.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/orders";
    }
}
