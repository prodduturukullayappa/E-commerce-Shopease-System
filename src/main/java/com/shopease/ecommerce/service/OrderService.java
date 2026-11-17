package com.shopease.ecommerce.service;

import com.shopease.ecommerce.model.*;
import com.shopease.ecommerce.repository.OrderRepository;
import com.shopease.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    /**
     * Place order from Shopping Cart
     */
    @Transactional
    public Order placeOrder(User user, String shippingAddress, String phone,
                            String paymentMethod, String paymentReference) {
        validateOrderInputs(shippingAddress, phone, paymentMethod, paymentReference);

        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Your shopping cart is empty.");
        }

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for: " + product.getName());
            }
        }

        Order order = createBaseOrder(user, shippingAddress, phone, paymentMethod, paymentReference);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity(), product.getPrice());
            order.getItems().add(orderItem);
            total = total.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(user);
        return savedOrder;
    }

    /**
     * Direct "Buy Now" order for a single product without touching existing cart items
     */
    @Transactional
    public Order placeBuyNowOrder(User user, Long productId, int quantity,
                                  String shippingAddress, String phone,
                                  String paymentMethod, String paymentReference) {
        validateOrderInputs(shippingAddress, phone, paymentMethod, paymentReference);

        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Only " + product.getStock() + " units available in stock.");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        Order order = createBaseOrder(user, shippingAddress, phone, paymentMethod, paymentReference);
        OrderItem orderItem = new OrderItem(order, product, quantity, product.getPrice());
        order.getItems().add(orderItem);
        order.setTotalAmount(orderItem.getSubtotal());

        return orderRepository.save(order);
    }

    private void validateOrderInputs(String shippingAddress, String phone, String paymentMethod, String paymentReference) {
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Delivery address is required.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact phone number is required.");
        }
        if ("UPI".equalsIgnoreCase(paymentMethod) && (paymentReference == null || paymentReference.trim().isEmpty())) {
            throw new IllegalArgumentException("Please enter your UPI ID for UPI payment.");
        }
        if ("Card".equalsIgnoreCase(paymentMethod) && (paymentReference == null || paymentReference.trim().length() < 4)) {
            throw new IllegalArgumentException("Please enter valid Card details.");
        }
    }

    private Order createBaseOrder(User user, String shippingAddress, String phone,
                                  String paymentMethod, String paymentReference) {
        Order order = new Order();
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        order.setOrderNumber("ORD-" + datePart + "-" + randomPart);
        order.setUser(user);
        order.setShippingAddress(shippingAddress.trim());
        order.setPhone(phone.trim());
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        String method = (paymentMethod == null || paymentMethod.isBlank()) ? "Cash on Delivery" : paymentMethod.trim();
        order.setPaymentMethod(method);

        if ("Cash on Delivery".equalsIgnoreCase(method)) {
            order.setPaymentStatus("PENDING (COD)");
            order.setTransactionId("COD-" + randomPart);
        } else if ("UPI".equalsIgnoreCase(method)) {
            order.setPaymentStatus("PAID");
            order.setTransactionId("UPI-" + paymentReference.trim() + "-" + randomPart);
        } else if ("Card".equalsIgnoreCase(method)) {
            String digits = paymentReference.replaceAll("\s+", "");
            String last4 = digits.length() >= 4 ? digits.substring(digits.length() - 4) : digits;
            order.setPaymentStatus("PAID");
            order.setTransactionId("CARD-XXXX-" + last4 + "-" + randomPart);
        } else {
            order.setPaymentStatus("PAID");
            order.setTransactionId("TXN-" + randomPart);
        }
        return order;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
    }

    @Transactional
    public void cancelOrder(User user, Long orderId) {
        Order order = getOrderById(orderId);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to cancel this order.");
        }
        if (!order.isCancellable()) {
            throw new IllegalArgumentException("Orders that are " + order.getStatus() + " cannot be cancelled.");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus("CANCELLED");
        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            order.setPaymentStatus("REFUNDED");
        } else {
            order.setPaymentStatus("CANCELLED");
        }
        orderRepository.save(order);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = getOrderById(orderId);
        String oldStatus = order.getStatus();

        if ("CANCELLED".equalsIgnoreCase(newStatus) && !"CANCELLED".equalsIgnoreCase(oldStatus)) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
            if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                order.setPaymentStatus("REFUNDED");
            } else {
                order.setPaymentStatus("CANCELLED");
            }
        } else if ("DELIVERED".equalsIgnoreCase(newStatus) && "Cash on Delivery".equalsIgnoreCase(order.getPaymentMethod())) {
            order.setPaymentStatus("PAID");
        }

        order.setStatus(newStatus.toUpperCase());
        orderRepository.save(order);
    }

    public BigDecimal getTotalSales() {
        return orderRepository.calculateTotalSales();
    }

    public long getTotalOrdersCount() {
        return orderRepository.count();
    }
}
