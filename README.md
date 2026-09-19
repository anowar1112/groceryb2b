# GroceryB2B - Professional B2B Marketplace App

GroceryB2B is a modern, offline-first Android application designed for B2B grocery commerce. Built with **Kotlin** and **Jetpack Compose**, it follows **Clean Architecture** principles and a **Multi-Module** structure to ensure scalability, maintainability, and high performance.

---

## 🚀 Key Features

### 🛒 Customer Side
*   **Product Catalog:** Browse products by categories (Rice, Dal, Oil, etc.) with real-time search.
*   **Dynamic Discount System:** View products with industry-standard discount displays (Original Price vs. Discounted Price).
*   **Smart Cart & Checkout:** Seamlessly add items to the cart and place orders with local persistence.
*   **Order History:** Track previous orders and view detailed item lists and statuses.
*   **Profile Management:** Set up and manage shop information (Name, Mobile, Address).

### 🛡️ Admin & Management (Advanced)
*   **Centralized Admin Dashboard:** Real-time statistics including Total Orders, Pending Orders, Total Products, and Low-Stock alerts.
*   **Advanced Order Management:** Track and update order statuses (Pending, Confirmed, Delivered, Cancelled) with color-coded status filters.
*   **Product Inventory Control:** Professional form for adding and editing products with discount support and stock management.
*   **Permission Management System:**
    *   **Super Admin:** Fixed authority for the primary administrator (`01557775958`).
    *   **Role-Based Access Control (RBAC):** Grant specific permissions (Create, Update, Delete, Manage Orders) to other users.
    *   **Security Guards:** Automatic logic to prevent self-permission updates and protect Super Admin status.

---

## 🛠️ Tech Stack & Architecture

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Modern, declarative UI)
*   **Architecture:** Clean Architecture + Multi-module (Feature-based separation)
*   **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) (Dagger-based DI)
*   **Local Database:** [Room](https://developer.android.com/training/data-storage/room) (SQLite abstraction for offline-first support)
*   **Navigation:** [Jetpack Navigation Component](https://developer.android.com/guide/navigation) (Compose-friendly navigation)
*   **Asynchronous Tasks:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)

---

## 📂 Project Structure (Modularized)

*   **`:app`** — Main entry point and navigation graph wiring.
*   **`:core`**
    *   `core-common` — Shared utilities, Dispatchers, and Result wrappers.
    *   `core-network` — Session management, interceptors, and permission logic.
    *   `core-ui` — Global design system, theme, and reusable UI components.
    *   `core-database` — Room Entities, DAOs, and Database configuration.
*   **`:feature`**
    *   `auth` — Secure login logic with local OTP verification.
    *   `shop-setup` — New shop registration and onboarding flow.
    *   `home` — The primary business module (Refactored for efficiency):
        *   `catalog/` — Home screen and product discovery.
        *   `admin/` — Dashboard, Inventory, and Permission Control.
        *   `order/` — Customer order tracking and details.
        *   `checkout/` — Cart management and order placement.
        *   `profile/` — User account settings.

---

## ⚙️ Getting Started

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/anowar1112/groceryb2b.git
    ```
2.  **Open in Android Studio:** (Koala | 2024.1.1 or newer recommended).
3.  **Gradle Sync:** Allow the project to download necessary dependencies.
4.  **Run the App:** Deploy the `app` module to an emulator or a physical device.

---

## 📝 Notes & Security
*   **Offline First:** Currently, all data is stored locally using Room. A backend sync implementation is planned for future versions.
*   **Super Admin:** The mobile number `01557775958` is hardcoded with full authority to manage the system.
*   **Local Verification:** The OTP code `000000` is used for demonstration purposes in this MVP.

---

## 🛤️ Future Roadmap
1.  [ ] Real-time Backend Sync (Firebase/REST API).
2.  [ ] Real SMS OTP Integration.
3.  [ ] Payment Gateway Integration.
4.  [ ] Advanced Analytics and Sales Reports.

---
Developed with ❤️ by **Anowar Hossain**
