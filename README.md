# ShopEase - Full-Stack E-Commerce Shopping Website (v2)

## Key Features Implemented
1. **Separate Admin Login & Customer Login**:
   - **Customer Login (`/login`)**: Dedicated portal for shoppers (`ROLE_USER`).
   - **Admin Login (`/admin/login`)**: Dedicated portal for store administrators (`ROLE_ADMIN`).
2. **Admin Portal (`/admin/dashboard`)**:
   - **Add & Update Products**: Complete CRUD with category, price, stock, image URL, description, and homepage featured toggle.
   - **Admin Product Details (`/admin/products/{id}`)**: View full product specifications, creation timestamp, and inline stock controls.
   - **Admin Order Details (`/admin/orders/{id}`)**: View full order breakdown, customer shipping info, payment method/status/transaction ID, ordered items, and status updater.
3. **Customer Shopping Experience**:
   - **Add to Cart (`/cart`)**: Add items, adjust quantities (`+` / `-`), remove items, and view automatic subtotals.
   - **Direct Buy Now (`/checkout/buy-now`)**: Instant single-product checkout directly from homepage cards, catalog cards, or product details page.
   - **Payment Details (`/checkout` & `/orders`)**: Supports **Cash on Delivery (COD)**, **UPI**, and **Credit/Debit Card** with automatic transaction ID generation and payment receipt tracking in **My Orders**.

## How to Run in IntelliJ IDEA
1. Extract `ecommerce-shopping-website-v2.zip` and open the folder in **IntelliJ IDEA**.
2. Start **MySQL** on port `3306` and verify credentials in `src/main/resources/application.properties` (`root` / `root` by default).
3. Run `EcommerceApplication.java`.
4. Open `http://localhost:8080`:
   - **Admin Login**: Click **Admin Login** (`/admin/login`) and sign in with `admin@shopease.com` / `admin123`.
   - **Customer Login**: Click **Register** (`/register`) to create a customer account, then log in via **Customer Login** (`/login`).
