# MudrikaVyavastha – Backend

MudrikaVyavastha is a secure, scalable backend service built to power a personal finance management platform.  
It handles authentication, transaction management, analytics, media storage, and secure communication between the frontend and database.

---

## 🚀 Tech Stack

- **Backend Framework:** Spring Boot
- **Security:** Spring Security + JWT Authentication
- **Database:** PostgreSQL (Neon – Serverless)
- **ORM:** Spring Data JPA (Hibernate)
- **Password Encryption:** BCrypt
- **Media Storage:** Cloudinary
- **Build Tool:** Maven
- **Hosting:** Render
- **API Style:** RESTful APIs

---

## 🔐 Authentication & Security

- Stateless authentication using **JWT**
- Tokens validated via a custom `JWTRequestFilter`
- Secure password hashing using **BCrypt**
- Role-based endpoint protection
- CORS configured for:
  - Localhost (development)
  - Netlify (production frontend)

---

## ☁️ Media Management (Cloudinary)

MudrikaVyavastha uses **Cloudinary** for secure and scalable cloud-based media storage.

### Use Cases
- Uploading and storing user-related images/icons
- Handling media independently of application server
- Optimized delivery via CDN

### Benefits
- No local file storage dependency
- Automatic image optimization
- Scalable and production-ready

---

## 🗄️ Database

- **PostgreSQL hosted on Neon**
- Cloud-native, serverless database
- Optimized connection pooling
- Environment-based configuration

---

## 📂 Core Features

- User Registration & Login
- JWT-based Authorization
- Income & Expense Management
- Category-wise Transaction Tracking
- Financial Aggregation APIs (Balance, Income, Expense)
- Secure Media Uploads via Cloudinary
- Secure Email & File-related endpoints
- Health & Status monitoring endpoints

---

## 📁 Project Structure

src/main/java
├── configs → Security, CORS & Cloudinary configuration
├── controllers → REST controllers
├── service → Business logic
├── repository → JPA repositories
├── security → JWT filters & utils
├── models → Entity classes
└── dto → Request / Response DTOs

---

## ⚙️ Environment Variables

Set the following variables on **Render**:

```env
DB_URL=jdbc:postgresql://<neon-db-url>
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

JWT_SECRET=your_jwt_secret

CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
▶️ Run Locally
mvn clean install
mvn spring-boot:run
Backend will start on:

http://localhost:8080
