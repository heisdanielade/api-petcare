# PamiętamPsa (Pet Care Tracker)

**PamiętamPsa** or **PamPsa** shortened (Polish for _"I remember the dog"_) powered by **Spring Boot**, to offer REST APIs that allow users to manage their pets, receive notifications, and track key pet care activities. Designed with simplicity and real-life usability in mind, this project supports modern authentication and a growing feature set.

## Technologies & Tools

| Layer/Service | Technology                    |
| ------------- | ----------------------------- |
| Language      | Java, Bash (startup script)   |
| Framework     | Spring Boot 3.4.3             |
| Database      | PostgreSQL with JPA/Hibernate |
| Email         | Spring Mail                   |
| Caching       | Caffeine                      |
| Security      | Spring Security with JWT auth |
| File Storage  | Cloudinary integration        |
| Deployment    | Docker, Maven, Railway        |
| Documentation | OpenAPI/Swagger               |

## Key Features

1. **User Management**

   - Registration with email verification
   - JWT-based authentication
   - Role-based authorization (User/Admin)

2. **Pet Management**

   - CRUD operations for pets
   - Pet profile with details and photos
   - Multi-pet support per user

3. **Email Notifications**

   - Registration confirmation
   - Email verification
   - Pet-related notifications

4. **File Management**
   - Support for pet photos/documents
   - File size limit: 3MB
   - Cloudinary integration for reliable storage

## API Overview

The API is versioned and documented with Swagger UI at `/swagger-ui.html`, which provides a visual interface to explore endpoints.

> 🔐 Access to Swagger docs is restricted based on user roles.

Frontend URL: [pamietampsa.app](https://pamietampsa.netlify.app/)

### API Endpoints

#### Authentication (`/v1/auth`)

- `POST /signup` - Register new user
- `POST /login` - Authenticate user
- `POST /verify-email` - Verify email address
- `POST /resend-verification` - Resend verification email

#### Users (`/v1/users`)

- CRUD operations for user management
- Profile updates
- User preferences

#### Pets (`/v1/pets`)

- CRUD operations for pet management
- Pet profile updates
- Photo management

#### Admin (`/v1/admin`)

- User management
- System monitoring
- Administrative tasks

## Design Decisions

1. **Spring Boot 3.x**

   - Latest LTS version for optimal performance and security
   - Native support for modern Java features

2. **PostgreSQL**

   - Robust ACID compliance
   - Advanced data types and query capabilities
   - Excellent performance for relational data

3. **JWT Authentication**

   - Stateless authentication for scalability
   - Reduced server-side storage requirements
   - Suitable for microservices architecture

4. **Cloudinary Integration**

   - Reliable file storage and delivery
   - Image optimization and transformation
   - CDN capabilities

5. **Caffeine Cache**
   - High-performance, near optimal caching
   - Lower memory footprint
   - Better hit rates than traditional caches

## Configuration

The application uses YAML configuration with environment-specific profiles:

- `application.yml` - Base configuration
- `application-dev.yml` - Development settings
- `application-prod.yml` - Production settings

Environment variables should be set in `.env` file (not tracked in git).

## Getting Started

1. Clone the repository
2. Configure `.env` file with required variables
3. Run PostgreSQL database
4. Execute: `./mvnw spring-boot:run`

## Security Considerations

- Email verification required for new accounts
- JWT tokens with appropriate expiration
- CSRF protection enabled
- File upload size limits
- Input validation on all endpoints

## Future Improvements

- Health monitoring endpoints
- Enhanced caching strategies
- Audit logging

---
