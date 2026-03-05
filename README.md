# 💳 Payment RBAC API

A Spring Boot REST API for secure payment processing with role-based access control (RBAC), Stripe payment integration, QR code generation, and PDF receipt generation.

This project demonstrates a production-style backend architecture with JWT authentication, Redis-based token blacklist, and Stripe webhook handling.

---

# 🚀 Features

## 🔐 Authentication & Security

* JWT authentication
* Role-Based Access Control (RBAC)
* Redis-based JWT blacklist (logout support)
* Secure endpoint access

## 💳 Payments

* Stripe Checkout Session creation
* Payment status tracking
* Stripe webhook integration

## 📱 QR Code Payments

* Generate QR codes for Stripe checkout URLs
* Users can scan and pay via mobile

## 🧾 Receipts

* Automatic PDF receipt generation after successful payment
* Optional email delivery

## 📊 Order Management

* Create payment orders
* View order history
* Track payment status

---

# 🏗 Architecture Overview

Client
↓
Spring Boot REST API
↓
Security Layer (JWT + RBAC)
↓
Order Service
↓
Stripe Payment Service
↓
QR Code Generator
↓
Receipt Generator
↓
PostgreSQL + Redis

---

# 📦 Technologies Used

* Java 17
* Spring Boot
* Spring Security
* JWT (jjwt)
* Stripe Java SDK
* Redis
* PostgreSQL
* ZXing (QR code generation)
* OpenPDF (PDF generation)
* Docker
* Swagger / OpenAPI

---

# 📂 Project Structure

payment-rbac-api
├── docker
│   └── docker-compose.yml
├── src
│   ├── main
│   │   ├── java/com/aynur/payment
│   │   │   ├── config
│   │   │   ├── security
│   │   │   ├── user
│   │   │   ├── order
│   │   │   ├── payment
│   │   │   ├── qrcode
│   │   │   ├── receipt
│   │   │   ├── domain
│   │   │   └── common
│   │   └── resources
│   │       └── application.yml
├── pom.xml
└── README.md

---

# 🔐 Roles

| Role   | Permissions              |
| ------ | ------------------------ |
| VIEWER | View orders and receipts |
| EDITOR | Create orders            |
| ADMIN  | Full system access       |

---

# 📡 API Endpoints

## Authentication

POST /auth/register
POST /auth/login
POST /auth/logout

## Orders

POST /orders
GET /orders/history
GET /orders/{id}

## Admin

GET /admin/orders

## QR Code

GET /orders/{id}/qrcode

## Receipt

GET /orders/{id}/receipt

## Stripe Webhook

POST /webhooks/stripe

---

# ⚙️ Running the Project

## 1️⃣ Clone repository

git clone https://github.com/your-username/payment-rbac-api.git

## 2️⃣ Start infrastructure

docker-compose up -d

This will start:

* PostgreSQL
* Redis

## 3️⃣ Run Spring Boot application

mvn spring-boot:run

---

# 📖 Swagger Documentation

After starting the application, open:

http://localhost:8080/swagger-ui

This allows you to test all API endpoints.

---

# 💡 Future Improvements

* Payment link expiration
* Email notifications
* Admin dashboard
* Microservice architecture
* Payment analytics

---

# 👨‍💻 Author

Aynur
