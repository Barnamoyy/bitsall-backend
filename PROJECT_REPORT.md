# BitsAll - Campus Management System
## Project Report

### Student Information
**Project Name:** BitsAll - Integrated Campus Management Platform  
**Technology Stack:** Java Spring Boot, PostgreSQL, Azure Blob Storage  
**Date:** November 2025

---

## 1. Executive Summary

BitsAll is a comprehensive campus management system designed to streamline communication and resource sharing within a university environment. The platform provides three core functionalities: announcements management, carpool coordination, and a pawn shop system for student-to-student transactions. This report focuses on the backend implementation using modern Java technologies and cloud services.

---

## 2. Technology Stack

### 2.1 Core Technologies
- **Java 17+**: Modern Java version with improved performance and language features
- **Spring Boot 3.x**: Framework for building production-ready applications
- **Spring Data JPA**: Database access and ORM (Object-Relational Mapping)
- **PostgreSQL**: Relational database for persistent data storage
- **Maven**: Build automation and dependency management

### 2.2 Cloud Services
- **Azure Blob Storage**: Cloud storage for images and file uploads
- **Docker**: Containerization for consistent deployment environments

### 2.3 Security & Validation
- **Spring Security**: Authentication and authorization framework
- **Jakarta Validation**: Input validation and constraint checking
- **BCrypt**: Password hashing algorithm

---

## 3. System Architecture

### 3.1 Layered Architecture
The application follows a standard layered architecture pattern:

```
Controller Layer → Service Layer → Repository Layer → Database
```

**Benefits of this approach:**
- Separation of concerns
- Easy to test and maintain
- Clear responsibility boundaries
- Reusable business logic

### 3.2 Package Structure
```
com.bitsall/
├── controller/     # REST API endpoints
├── service/        # Business logic
├── repository/     # Database access
├── model/
│   ├── entity/     # Database entities
│   ├── dto/        # Data transfer objects
│   └── enums/      # Enumeration types
└── config/         # Configuration classes
```

---

## 4. Core Modules

### 4.1 Announcement System

**Purpose:** Enable faculty, department heads, club heads, and student council to post announcements with granular visibility control.

**Key Features:**
- Role-based announcement creation
- Visibility filtering (Everyone, Clubs, Departments, Student Council)
- Image upload support via Azure Blob Storage
- Automatic authorization checks
- Audit logging for compliance

**Implementation Highlights:**

**Entity Structure:**
```java
@Entity
public class Announcement {
    private UUID id;
    private String title;
    private String body;
    private LocalDateTime datePosted;
    private LocalDateTime expiryDate;
    private List<String> links;
    private VisibilityType visibility;
    private DepartmentType department;
    private ClubType club;
    private String imageUrl;
    private User createdBy;
}
```

**Authorization Logic:**
- Only authorized roles (Faculty, Club Head, Department Head, Student Council, Admin) can create announcements
- Department Heads can only post to their assigned departments
- Club Heads can only post to their assigned clubs
- Student Council members can create student council-specific announcements
- Users can only delete their own announcements

**Visibility Filtering:**
- Custom JPQL queries filter announcements based on user's clubs, departments, and role
- Efficient database-level filtering reduces data transfer
- Announcements marked as "EVERYONE" are visible to all authenticated users

### 4.2 Carpool System

**Purpose:** Facilitate ride-sharing among students and faculty for campus commutes.

**Key Features:**
- Two carpool types: Looking for passengers, Looking for ride
- Location-based matching (departure and destination)
- Date and time scheduling
- Seat availability tracking
- Contact information sharing

**Entity Structure:**
```java
@Entity
public class CarpoolRequest {
    private UUID id;
    private CarpoolType type;
    private String departureLocation;
    private String destinationLocation;
    private LocalDateTime departureTime;
    private int availableSeats;
    private User createdBy;
}
```

### 4.3 Pawn Shop System

**Purpose:** Enable students to buy, sell, or exchange items within the campus community.

**Key Features:**
- Item listing with images
- Price negotiation support
- Category-based organization
- Item status tracking (available, sold, reserved)
- Contact details for transactions

**Entity Structure:**
```java
@Entity
public class PawnItem {
    private UUID id;
    private String itemName;
    private String description;
    private double price;
    private String category;
    private String condition;
    private String imageUrl;
    private User createdBy;
}
```

### 4.4 User Management

**Purpose:** Handle user authentication, authorization, and profile management.

**Key Features:**
- Multiple user roles (Student, Faculty, Department Head, Club Head, Student Council, Admin)
- Department and club affiliations
- Email-based authentication
- Password encryption using BCrypt
- Profile verification system

**Entity Structure:**
```java
@Entity
public class User {
    private UUID id;
    private String fullName;
    private String email;
    private String password;
    private UserRole role;
    private List<DepartmentType> departments;
    private List<ClubType> clubs;
    private String phoneNumber;
    private boolean verified;
}
```

---

## 5. Database Design

### 5.1 Entity Relationships

**User Entity (Central):**
- One-to-Many with Announcements
- One-to-Many with CarpoolRequests
- One-to-Many with PawnItems

**Benefits of UUID as Primary Key:**
- Globally unique identifiers
- Better for distributed systems
- No sequential information leakage
- Easier database merging and replication

### 5.2 Enum Types
The system uses enums for type-safe, predefined values:

- **UserRole:** STUDENT, FACULTY, DEPARTMENT_HEAD, CLUB_HEAD, STUDENT_COUNCIL, ADMIN
- **DepartmentType:** CSE, ECE, MECHANICAL, CIVIL, ELECTRICAL, CHEMICAL, BIOTECH, GENERAL
- **ClubType:** TECH_CLUB, MUSIC_CLUB, DRAMA_CLUB, SPORTS_CLUB, LITERARY_CLUB, PHOTOGRAPHY_CLUB, NONE
- **VisibilityType:** EVERYONE, SUBSCRIBERS_ONLY, CLUBS, DEPARTMENTS, STUDENT_COUNCIL, GENERAL
- **CarpoolType:** LOOKING_FOR_PASSENGERS, LOOKING_FOR_RIDE

---

## 6. API Design

### 6.1 RESTful Endpoints

**Announcements API:**
```
POST   /api/announcements          # Create announcement (with image)
GET    /api/announcements          # Get filtered announcements
GET    /api/announcements/{id}     # Get specific announcement
DELETE /api/announcements/{id}     # Delete own announcement
```

**Carpool API:**
```
POST   /api/carpool                # Create carpool request
GET    /api/carpool                # Get all carpool requests
GET    /api/carpool/{id}           # Get specific request
DELETE /api/carpool/{id}           # Delete own request
```

**Pawn Shop API:**
```
POST   /api/pawnshop               # Create item listing (with image)
GET    /api/pawnshop               # Get all items
GET    /api/pawnshop/{id}          # Get specific item
DELETE /api/pawnshop/{id}          # Delete own item
```

**User API:**
```
POST   /api/users/register         # Register new user
POST   /api/users/login            # User authentication
GET    /api/users/{id}             # Get user profile
```

**Enums API:**
```
GET    /api/enums/roles            # Get all user roles
GET    /api/enums/departments      # Get all departments
GET    /api/enums/clubs            # Get all clubs
GET    /api/enums/visibility       # Get visibility types
GET    /api/enums/carpool-types    # Get carpool types
```

### 6.2 Request/Response Format

**Multipart Form Data for File Uploads:**
```
Content-Type: multipart/form-data

announcement: {JSON data}
image: {binary file}
```

**JSON for Data Transfer:**
All endpoints accept and return JSON format for easy integration with frontend frameworks.

---

## 7. Security Implementation

### 7.1 Authentication
- Email and password-based authentication
- Passwords hashed using BCrypt (industry standard)
- Session management via Spring Security
- JWT tokens can be integrated for stateless authentication

### 7.2 Authorization

**Role-Based Access Control (RBAC):**
- Different permissions for different user roles
- Method-level security checks
- Request validation against authenticated user

**Announcement Creation Rules:**
- Faculty: Can create public announcements
- Department Heads: Can create department-specific announcements (only their departments)
- Club Heads: Can create club-specific announcements (only their clubs)
- Student Council: Can create student council announcements
- Admins: Can create any type of announcement

### 7.3 Data Validation
- Jakarta Validation annotations on DTOs
- @NotBlank for required fields
- @Email for email format validation
- @NotNull for non-nullable fields
- Custom business logic validation in service layer

### 7.4 Security Exception Handling
```java
@ExceptionHandler(SecurityException.class)
public ResponseEntity<String> handleSecurityException(SecurityException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
}
```

---

## 8. File Upload System

### 8.1 Azure Blob Storage Integration

**Purpose:** Store user-uploaded images securely in the cloud.

**Implementation:**
- AzureBlobService handles all blob operations
- Files organized by module (announcements, pawnshop)
- Unique file names prevent conflicts
- Returns public URL for image access

**Benefits:**
- Scalable storage solution
- No server disk space concerns
- CDN integration possible
- Automatic backups and redundancy

**Service Methods:**
```java
public String uploadFile(MultipartFile file, String containerName, String fileName)
public void deleteFile(String fileUrl)
```

---

## 9. Advanced Features Implemented

### 9.1 Visibility Filtering with JPQL

Custom query for efficient database-level filtering:

```java
@Query("SELECT DISTINCT a FROM Announcement a WHERE " +
       "(a.visibility = 'EVERYONE' OR a.visibility IS NULL) " +
       "OR (a.visibility = 'CLUBS' AND a.club IN :userClubs) " +
       "OR (a.visibility = 'DEPARTMENTS' AND a.department IN :userDepartments) " +
       "OR (a.visibility = 'STUDENT_COUNCIL' AND :isStudentCouncil = true) " +
       "ORDER BY a.datePosted DESC")
```

**Advantages:**
- Single database query instead of filtering in application
- Reduced memory usage
- Faster response times
- Scales well with large datasets

### 9.2 Audit Logging

Implemented SLF4J logging for critical operations:
- Announcement creation and deletion
- User authentication attempts
- Authorization failures
- Data modification events

**Example:**
```java
log.info("Announcement created: id={}, createdBy={}, role={}, visibility={}", 
    saved.getId(), user.getId(), user.getRole(), saved.getVisibility());
```

### 9.3 Exception Handling Strategy

**Three-Tier Exception Handling:**
1. **SecurityException:** For authorization failures (403 Forbidden)
2. **IllegalArgumentException:** For invalid data (400 Bad Request)
3. **General Exception:** For unexpected errors (500 Internal Server Error)

---

## 10. Design Patterns Used

### 10.1 Dependency Injection
- Constructor-based injection using Lombok's @RequiredArgsConstructor
- Promotes loose coupling and testability
- Spring manages bean lifecycle

### 10.2 Data Transfer Object (DTO) Pattern
- Separate DTOs from entity classes
- Controls what data is exposed via API
- Prevents over-fetching and security leaks

### 10.3 Repository Pattern
- Abstracts database access
- Easy to switch database implementations
- Supports custom queries via @Query annotation

### 10.4 Builder Pattern
- Lombok's @Builder for clean object creation
- Immutable object construction
- Readable code for complex objects

### 10.5 Service Layer Pattern
- Business logic separated from controllers
- Reusable across multiple endpoints
- Easier unit testing

---

## 11. Configuration Management

### 11.1 Application Properties
Externalized configuration in `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/bitsall
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Azure Blob Storage
azure.storage.connection-string=${AZURE_CONNECTION_STRING}
azure.storage.container-name=bitsall-uploads

# File Upload Limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

**Benefits:**
- Environment-specific configurations
- Secrets managed via environment variables
- Easy deployment across environments

---

## 12. Testing Considerations

### 12.1 Unit Testing Strategy
- Service layer methods should be unit tested
- Mock repository dependencies
- Test authorization logic separately
- Validate business rules

### 12.2 Integration Testing
- Test complete request-response cycle
- Verify database operations
- Check file upload functionality
- Validate security constraints

### 12.3 Test Coverage Goals
- Service layer: 80%+ coverage
- Controller layer: 70%+ coverage
- Repository layer: Covered via integration tests

---

## 13. Deployment Architecture

### 13.1 Docker Containerization

**Dockerfile:**
- Multi-stage build for optimized image size
- Java 17 runtime environment
- Exposed port 8080 for HTTP traffic

**Docker Compose:**
- PostgreSQL database container
- Application container
- Network configuration
- Volume mounting for persistence

### 13.2 Environment Variables
Required for production deployment:
```bash
DB_USERNAME=postgres
DB_PASSWORD=secure_password
AZURE_CONNECTION_STRING=DefaultEndpointsProtocol=https;...
JWT_SECRET=your_jwt_secret_key
```

---

## 14. Performance Optimizations

### 14.1 Database Optimizations
- Indexed columns: email, id (UUID)
- Lazy loading for ManyToOne relationships
- JPQL queries with proper JOIN strategies
- Connection pooling via HikariCP

### 14.2 Caching Opportunities
- Static data (enums) can be cached
- User profile caching with TTL
- Frequently accessed announcements

### 14.3 Query Optimization
- Fetch only required fields using DTOs
- Pagination for large result sets (future enhancement)
- Avoid N+1 query problems with proper fetch strategies

---

## 15. Challenges Faced and Solutions

### 15.1 Challenge: Complex Visibility Logic
**Problem:** Different users should see different announcements based on their role, clubs, and departments.

**Solution:** Implemented custom JPQL queries with dynamic parameters that filter at database level, ensuring efficient and secure data access.

### 15.2 Challenge: Role-Based Authorization
**Problem:** Multiple user roles with different permissions for creating announcements.

**Solution:** Created a centralized authorization check method in the service layer that validates user permissions based on visibility type and user attributes.

### 15.3 Challenge: File Upload Integration
**Problem:** Storing images locally would not scale and complicate container deployments.

**Solution:** Integrated Azure Blob Storage for cloud-based file storage with automatic URL generation for frontend access.

### 15.4 Challenge: Security Context Management
**Problem:** Ensuring logged-in user cannot create announcements on behalf of others.

**Solution:** Extract authenticated user from Spring Security context and validate against request data before processing.

---

## 16. Future Enhancements

### 16.1 Notification System
- Email notifications for new announcements
- Push notifications for mobile apps
- Subscription-based notification preferences

### 16.2 Search Functionality
- Full-text search for announcements
- Filter by date, department, club
- Advanced search with multiple criteria

### 16.3 Analytics Dashboard
- Announcement view counts
- Popular carpool routes
- Most active users
- Department-wise engagement metrics

### 16.4 Real-Time Features
- WebSocket integration for live updates
- Chat functionality for carpool coordination
- Real-time notification delivery

### 16.5 API Rate Limiting
- Prevent abuse with rate limiting
- DDoS protection
- Fair usage policies

---

## 17. Learning Outcomes

### 17.1 Technical Skills Gained
- Hands-on experience with Spring Boot framework
- Understanding of RESTful API design principles
- Database design and JPA relationship mapping
- Cloud services integration (Azure)
- Security implementation in web applications

### 17.2 Software Engineering Practices
- Layered architecture implementation
- Design patterns application
- Exception handling strategies
- Logging and debugging techniques
- Version control with Git

### 17.3 Problem-Solving Skills
- Breaking down complex requirements into modules
- Implementing role-based access control
- Optimizing database queries for performance
- Handling edge cases and validation

---

## 18. Conclusion

BitsAll successfully demonstrates a modern, scalable backend system for campus management. The implementation follows industry best practices including:

- **Clean Architecture:** Proper separation of concerns with layered design
- **Security First:** Comprehensive authentication and authorization
- **Scalability:** Cloud storage and efficient database queries
- **Maintainability:** Well-structured code with clear responsibilities
- **Extensibility:** Easy to add new features and modules

The project provides a solid foundation for campus-wide communication and resource sharing, with room for expansion into additional features like event management, course registration, and student forums.

### Key Achievements
✓ Three fully functional modules (Announcements, Carpool, Pawn Shop)  
✓ Role-based access control with 6 user roles  
✓ Cloud-integrated file upload system  
✓ Secure REST API with proper validation  
✓ Dockerized deployment setup  
✓ Comprehensive audit logging  
✓ Visibility filtering for targeted communication  

The system is production-ready and can be deployed on any cloud platform supporting Docker containers and PostgreSQL databases.

---

## 19. References

1. Spring Boot Documentation - https://spring.io/projects/spring-boot
2. Spring Security Reference - https://docs.spring.io/spring-security/reference/
3. Azure Blob Storage Java SDK - https://learn.microsoft.com/en-us/azure/storage/
4. PostgreSQL Documentation - https://www.postgresql.org/docs/
5. RESTful API Design Best Practices - https://restfulapi.net/
6. Clean Architecture Principles - Robert C. Martin

---

**Submitted By:** [Your Name]  
**Roll Number:** [Your Roll Number]  
**Department:** [Your Department]  
**Date:** November 15, 2025

