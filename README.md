# 🛡️ Family Health Vault (FamHealth)

> **Production-Ready Medical Records Vault, Vitals Telemetry & Medication Engine for Elderly Care.**

Built with **Java 21, Spring Boot 3.3.5, Spring Security 6 (JWT), Spring Data JPA, PostgreSQL, AWS S3, and a modern responsive Single Page Application UI**.

---

## 🌟 Key Features

* **Multi-Tenant User & Patient Management**: Full family hierarchy where caregivers manage multiple elderly parents.
* **Granular Role-Based Access Control (RBAC)**:
  * `ROLE_ADMIN`: Full system management, user registration, and document deletion.
  * `ROLE_VIEWER`: Read access to medical histories, trends, and medication schedules.
  * `ROLE_NURSE`: Clinical least-privilege mode. Strictly permitted to log daily vitals (`POST /api/vitals`), with all document and medication records protected.
* **Longitudinal Vitals Telemetry**: Track Systolic/Diastolic blood pressure and fasting blood sugar with real-time Chart.js visual trendlines and time-series date filtering.
* **Automated Medication Engine**:
  * Tracks pill inventory and strip capacity.
  * **Hourly Background Job (`@Scheduled`)**: Automated cron runs every hour to check reminder schedules and simulate SMS alerts.
  * **Low-Stock Warning**: Automatically alerts family whenever inventory drops below 5 pills.
  * **One-Click Dose & Refill Actions**: `Take Dose (-1)` and `Refill (+30)` buttons with live stock progress indicators.
* **AWS S3 Document Vault**: Secure multi-part upload for `LAB_REPORT`, `PRESCRIPTION`, and `SCAN` files with search tagging and transactional deletion.
* **Emergency 24-Hour Public Sharing**: Generates time-limited, cryptographically secure UUID links for emergency room doctors without requiring login credentials.

---

## 🏗️ Architecture

```
Client (Mobile / Desktop)
   │
   ▼
Spring Security 6 (JWT Filter)
   │
   ├── /api/auth/**      --> Authentication & Registration
   ├── /api/patients/**  --> Patient Profile Management
   ├── /api/vitals/**    --> Vitals Telemetry (NURSE / ADMIN / VIEWER)
   ├── /api/medications  --> Prescription Tracking & Refills
   ├── /api/documents/** --> AWS S3 File Storage & Postgres Metadata
   └── /api/public/**    --> 24-Hour Emergency Doctor Portal
```

---

## 🚀 Quick Start Guide

### Prerequisites
* **Java 21+**
* **Maven 3.8+**
* **Docker** or local **PostgreSQL** instance

### 1. Start PostgreSQL with Docker
```bash
docker run --name famhealth-postgres -e POSTGRES_DB=famhealth_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:latest
```

### 2. Run the Spring Boot Application
```bash
mvn clean spring-boot:run
```

### 3. Open the Web Application
Open your browser and visit:
👉 **`http://localhost:8080`**

### Pre-Configured Demo Accounts:
| Role | Username | Password |
|:---|:---|:---|
| **Admin** | `admin` | `Admin@12345` |
| **Nurse** | `nurse` | `Nurse@12345` |
| **Viewer** | `viewer` | `Viewer@12345` |

---

## 🔒 Security Configuration
* JWT tokens are generated using HMAC-SHA256 with 24-hour expiration.
* Passwords are cryptographically salted and hashed using `BCryptPasswordEncoder`.
* Standardized exception handling via `@RestControllerAdvice` returning uniform `ApiResponse<T>` envelopes.
