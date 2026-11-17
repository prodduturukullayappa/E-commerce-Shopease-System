## Project Description
ShopEase is a full-stack e-commerce web application built with Java, Spring Boot, Thymeleaf, and MySQL. It features separate Admin and Customer portals, inventory management, secure authentication, persistent shopping carts, and express "Buy Now" checkout. The platform supports multi-option payment processing including Cash on Delivery, UPI, and Card, alongside real-time order tracking and sales analytics.   

## 🚀 Features
🛍️ Product & Catalog Management ✔ Browse products organized by categories with Indian Rupee (₹) pricing. ✔ Real-time stock tracking and inventory updates.

👤 User Authentication & Profiles ✔ Secure customer registration and login workflows with credential validation. ✔ Persistent client-side session management for seamless cart retention.

🛒 Shopping Cart & Checkout System ✔ Add, update quantities, and remove items from the shopping cart. ✔ "Buy Now" direct checkout option for instant single-item purchases.

💳 Order & Payment Management ✔ Support for multiple payment methods including UPI, Cards, Net Banking, and Cash on Delivery (COD). ✔ Automated order processing with atomic transactions and real-time inventory stock deduction.

📊 Admin Management Dashboard ✔ Password-protected admin portal for complete system control. ✔ Add new products, track overall sales revenue, and manage order statuses (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED).

##  📁Project structture
```
ecommerce-shopping-website/
├── pom.xml                                      
├── schema.sql                                 
├── README.md                                    
└── src/main/
    ├── java/com/shopease/ecommerce/
    │   ├── EcommerceApplication.java            
    │   ├── model/
    │   │   ├── User.java                        
    │   │   ├── Category.java                    
    │   │   ├── Product.java                     
    │   │   ├── CartItem.java                    
    │   │   ├── Order.java                    
    │   │   └── OrderItem.java                   
    │   ├── repository/
    │   │   ├── UserRepository.java              
    │   │   ├── CategoryRepository.java          
    │   │   ├── ProductRepository.java           
    │   │   ├── CartRepository.java              
    │   │   └── OrderRepository.java             
    │   ├── service/
    │   │   ├── CustomUserDetailsService.java    
    │   │   ├── SecurityConfig.java              
    │   │   ├── UserService.java                 
    │   │   ├── ProductService.java              
    │   │   ├── CartService.java                 
    │   │   └── OrderService.java                
    │   └── controller/
    │       ├── GlobalModelAdvice.java           
    │       ├── HomeProductController.java       
    │       ├── AuthProfileController.java       
    │       ├── CartOrderController.java         
    │       └── AdminController.java             
    └── resources/
        ├── application.properties               
        ├── static/
        │   ├── css/style.css                    
        │   └── js/app.js                        
        └── templates/
            ├── fragments.html                   
            ├── index.html                       
            ├── login.html                    
            ├── admin-login.html                 
            ├── register.html                    
            ├── products.html                    
            ├── product-details.html             
            ├── cart.html                        
            ├── checkout.html                    
            ├── my-orders.html                   
            ├── profile.html                     
            ├── admin-dashboard.html             
            ├── admin-product-details.html       
            └── admin-order-details.html         
```
## 🛠️ Installation & Execution Instructions

### Prerequisites
1. **Java Development Kit (JDK) 21**
2. **Apache Maven**

### Step-by-Step Execution
1. Extract `ecommerce-project.zip` to your project folder.
2. Build the project:
   ```cmd
   mvn clean install
   ```
3. Run the Spring Boot application:
   ```cmd
   mvn spring-boot:run
   ```
4. Access via browser
   http://localhost:8080

## how the System works
1. Users authenticate securely through dedicated Customer and Admin login portals backed by Spring Security.
2. Customers browse the MySQL catalog, add items to their persistent shopping cart, or use express "Buy Now".
3. Shoppers complete checkout by providing delivery details and choosing payment methods like COD, UPI, or Card.
4. Administrators manage inventory, update product stocks, track sales KPIs, and process customer orders from the dashboard.

<img width="1920" height="1080" alt="Screenshot (97)" src="https://github.com/user-attachments/assets/c3edcbfb-d56c-442e-a56a-ab271f086b32" />

## 🏗️ Future Enhanecements
1. Integrate live payment gateways like Razorpay or Stripe along with automated email notifications for order confirmations.
2. Add customer-facing features including product reviews, star ratings, and a persistent wishlist for saving items.
3. Upgrade image management by migrating from static URLs to direct admin file uploads backed by AWS S3 or Cloudinary.
4. Decouple the backend into RESTful APIs to support a modern React or Angular frontend alongside a mobile application.
## 🏆  Why use this project
●This project is a lightweight, zero-setup full-stack e-commerce platform built with Java, Spring Boot, and Mysql that features role-based access, a shopping cart, multi-channel payments, and an admin dashboard

## 📞 Contact
For any issues or suggestions, feel free to reach out:
📧 Email: prodduturukullayappa@gmail.com
