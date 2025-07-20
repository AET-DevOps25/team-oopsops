# Redacta - Comprehensive Project Documentation

## Table of Contents

1. [Project Description](#1-project-description)
2. [Quick Start and Technical Details](#2-quick-start-and-technical-details)

---

## 1. Project Description

### Overview

**Redacta** is an AI-powered document anonymization and summarization platform designed to help organizations handle sensitive documents while ensuring compliance with privacy regulations. The platform combines intelligent document processing with user-friendly interfaces to provide an end-to-end solution for document privacy management.

### Pain Points

Redacta aims to address critical challenges faced by organizations handling sensitive documents:

- **Privacy Compliance**: Organizations struggle to comply with GDPR, HIPAA, and other privacy regulations when sharing or processing documents containing personal information
- **Manual Anonymization**: Traditional manual redaction processes are time-consuming, error-prone, and inconsistent
- **Document Processing Efficiency**: Large volumes of documents require significant human resources to review and anonymize
- **Information Sharing**: Organizations need to share documents for collaboration while protecting sensitive information
- **Audit Trail**: Need for maintaining proper documentation of anonymization processes for compliance purposes

### Key Features

#### 1. **Intelligent Document Upload**

- Support for PDF document formats with drag-and-drop interface
- Automatic document parsing and text extraction

#### 2. **AI-Powered Anonymization**

- **Three-tier anonymization levels:**
  - **Light**: Names and direct identifiers only
  - **Medium**: Names, contact details, and locations
  - **Heavy**: All potential personal and sensitive information
- Automatic detection of PII (Personally Identifiable Information)
- Smart replacement with contextually appropriate placeholders
- Real-time preview of anonymized content

#### 3. **Manual Review and Customization**

- Interactive document editor with highlighted sensitive sections
- Click-to-edit functionality for anonymized text
- Manual text selection for additional anonymization
- Visual differentiation between original (red) and anonymized (green) text

#### 4. **Document Summarization**

- AI-generated summaries with three length options (**short**, **medium**, **long**)
- Context-aware summarization that preserves key information
- Available for both original and anonymized documents
- Downloadable summary reports

#### 5. **Document Archive and Management**

- Complete document history tracking
- Search functionality by filename
- Status tracking (Original, Anonymized)

#### 6. **Secure Authentication and Authorization**

- OAuth2/JWT-based authentication via _Keycloak_
- Role-based access control
- Secure session management
- Multi-user support with isolated data

#### 7. **Chat for Document Interaction**

- Interactive chat widget for querying documents
- Context-aware responses based on document content
- Utilising RAG (Retrieval-Augmented Generation)

### Generative AI and RAG Implementation

#### **AI Anonymization Engine**

Redacta leverages **OpenAI GPT-4** models through a LangGraph-based workflow:

- **Structured Output Processing**: Uses Pydantic models to ensure consistent anonymization results
- **Context-Aware Detection**: AI first analyzes document context to identify sensitive information beyond simple pattern matching
- **Smart Replacement**: Generates contextually appropriate replacements for the identified terms (e.g., "Person A", "Location B", "Date C")
- **Multi-level Processing**: Dynamically adjusts detection sensitivity based on selected anonymization level

#### **RAG (Retrieval-Augmented Generation) for Summarization**

- **ChromaDB Vector Store**: Documents are chunked and embedded for semantic search
- **Context Retrieval**: Relevant document sections are retrieved based on summarization requirements
- **Conversation Chain**: Maintains context across multiple interactions with documents
- **Document Chat**: Users can interact with documents through natural language queries via the chat interface

### Functional User Flows

#### **Flow 1: Complete Document Anonymization**

1. **Upload**: User drags PDF file to upload area
2. **Processing**: System extracts text and displays document preview
3. **AI Analysis**: User selects anonymization level (Light/Medium/Heavy)
4. **Anonymization**: AI identifies and replaces sensitive information
5. **Review**: User reviews highlighted changes in document editor
6. **Manual Editing**: User can modify anonymized terms or add additional anonymizations
7. **Save**: System stores anonymization mappings and metadata
8. **Export**: User downloads anonymized PDF with proper formatting

#### **Flow 2: Document Summarization Workflow**

1. **Source Selection**: User chooses between original or anonymized document
2. **Summary Configuration**: Select summary length (short/medium/long)
3. **AI Processing**: System generates contextual summary
4. **Review**: User reviews generated summary in dedicated panel
5. **Export**: Download summary as standalone document

#### **Flow 3: Chat for Document Interaction**

1. **Chat Interface**: User opens chat widget for document interaction
2. **Query Input**: User types natural language questions about the document
3. **Context Retrieval**: System retrieves relevant document sections using RAG
4. **AI Response**: AI generates context-aware answers based on document content

---

## 2. Quick Start and Technical Details

### Quick Start Guide

#### Prerequisites

- Docker and Docker Compose installed OR Docker Desktop (Mac/Windows) installed
- OpenAI API key
- 8GB+ RAM recommended
- Following ports available:
  - 8000
  - 8081
  - 8085
  - 8091
  - 8092
  - 8094
  - 3000
  - 5432
  - 5050

#### Local Development Setup

1. **Clone the Repository**

```bash
git clone https://github.com/AET-DevOps25/team-oopsops.git
cd team-oopsops
```

2. **Environment Configuration**

```bash
# Create environment file
cp .env.example .env

# Set required environment variables in the .env file
OPENAI_API_KEY="your-openai-api-key"
```

3. **Start All Services**

```bash
# Launch complete stack
docker-compose up -d

# View logs
docker-compose logs -f
```

4. **Access the Platform**

- **Main Application**: http://localhost:3000
- **API Gateway**: http://localhost:8081
- **Database Admin**: http://localhost:5050 (admin@admin.com / admin)
- **Keycloak Admin**: http://localhost:8085 (admin / admin)

### Service Architecture and Ports

| Service                    | Port | Description                      | Health Check                          |
| -------------------------- | ---- | -------------------------------- | ------------------------------------- |
| **API Gateway** (Nginx)    | 8081 | Routes requests to microservices | http://localhost:8081                 |
| **Client** (React SPA)     | 3000 | Frontend application             | http://localhost:3000                 |
| **Document Service**       | 8091 | Document upload and management   | http://localhost:8091/actuator/health |
| **Authentication Service** | 8092 | User auth and JWT management     | http://localhost:8092/actuator/health |
| **Anonymization Service**  | 8094 | Document anonymization logic     | http://localhost:8094/actuator/health |
| **GenAI Service**          | 8000 | AI processing and RAG            | http://localhost:8000/health          |
| **PostgreSQL**             | 5432 | Primary database                 | `pg_isready -U dev_user`              |
| **PgAdmin**                | 5050 | Database administration          | http://localhost:5050                 |
| **Keycloak**               | 8085 | Identity and access management   | http://localhost:8085                 |

### API Documentation

#### Authentication Service (`/api/v1/authentication`)

- `POST /register` - User registration with Keycloak integration
- `POST /login` - User authentication, returns JWT tokens
- `POST /refresh` - Refresh access token using refresh token

#### Document Service (`/api/v1/documents`)

- `GET /` - List all user documents with metadata
- `GET /{id}` - Retrieve specific document by ID
- `POST /upload` - Upload PDF file for processing

#### Anonymization Service (`/api/v1/anonymization`)

- `GET /` - List user's anonymization records
- `POST /{documentId}/add` - Save anonymization for document
- `POST /replace` - Process text anonymization with term replacements
- `GET /{id}/download` - Download anonymized document as PDF

#### GenAI Service (`/api/v1/genai`)

- `POST /anonymize` – AI-powered anonymization with level selection.
Returns a list of terms to replace (e.g., { original: "John", replacement: "Person A" }) rather than generating a fully anonymized text.
- `POST /summarize` - Generate document summaries
- `POST /chat` - Interactive document chat using RAG
- `POST /documents/upload` - Upload documents to vector store

### Architecture Overview

#### **Microservices Architecture**

```
┌─────────────────┐    ┌──────────────────┐   
│   React Client  │────│   Nginx Gateway  │
│     (Port 3000) │    │    (Port 8081)   │    
└─────────────────┘    └──────────────────┘   
                                │
                    ┌───────────┼───────────┐
                    │           │           │
            ┌───────▼──┐ ┌──────▼──┐ ┌──────▼──────┐
            │Document  │ │Auth     │ │Anonymization│
            │Service   │ │Service  │ │Service      │
            │(8091)    │ │(8092)   │ │(8094)       │
            └──────────┘ └─────────┘ └─────────────┘
                    │           │           │
                    └───────────┼───────────┘
                                │
                    ┌───────────▼───────────┐
                    │     PostgreSQL        │
                    │      (Port 5432)      │
                    │  (Multi-database)     │
                    └───────────────────────┘

┌─────────────────┐    ┌──────────────────┐
│  GenAI Service  │────│   ChromaDB       │
│   (Port 8000)   │    │  Vector Store    │
│   (FastAPI)     │    │   (Embedded)     │
└─────────────────┘    └──────────────────┘
```

#### **Data Flow Architecture**

1. **Request Flow**: Client → Nginx → Microservice → Database
2. **Authentication Flow**: Client → Auth Service → Keycloak → JWT
3. **Document Processing**: Upload → Document Service → Text Extraction → Storage
4. **AI Processing**: Document → GenAI Service → OpenAI API → ChromaDB → Response
5. **Anonymization Flow**: Extracted Text + Level of Anonymization → GenAI Service → Anonymization Service to replace → GenAI → Response 

### Technology Stack

#### **Frontend**

- **Framework**: React 19.1.0 with TypeScript
- **Styling**: Tailwind CSS 3.4.17 with custom components
- **UI Library**: Radix UI components with shadcn/ui
- **State Management**: TanStack Query for API state management
- **Routing**: React Router DOM 7.6.2
- **Build Tool**: Vite 6.3.5
- **Form Handling**: React Hook Form with Zod validation

#### **Backend Services**

- **Framework**: Spring Boot 3.5.0 with Java 21
- **Security**: Spring Security with OAuth2 Resource Server
- **Database**: PostgreSQL with JPA/Hibernate
- **Documentation**: OpenAPI/Swagger integration
- **Monitoring**: Spring Actuator with Prometheus metrics and  and visualization in Grafana dashboards, Alerts Definition is in Prometheus and managed via Alertmanager
- **Build Tool**: Gradle 8.14

#### **GenAI Service**

- **Framework**: FastAPI 0.115.12 with Python 3.9+
- **AI/ML Libraries**:
  - LangChain 0.3.25 for LLM orchestration
  - LangGraph 0.4.8 for workflow management
  - ChromaDB 1.0.15 for vector storage
- **LLM Integration**: OpenAI GPT-4 via langchain-openai
- **Document Processing**: PyPDF 5.7.0, ReportLab 4.4.2

#### **Infrastructure**

- **Containerization**: Docker with multi-stage builds
- **Orchestration**: Docker Compose for local, Kubernetes for production
- **Reverse Proxy**: Nginx for API Gateway
- **Database**: PostgreSQL 14 Alpine
- **Identity Provider**: Keycloak 22.0.3
- **Monitoring**: Prometheus + Grafana + Alertmanager in Kubernetes (configured but disabled in docker-compose)

#### **DevOps & CI/CD**

- **Container Registry**: GitHub Container Registry (GHCR)
- **CI/CD**: GitHub Actions with automated testing
- **Deployment**: AWS EC2 + Rancher Kubernetes
- **Load Balancing**: Traefik for production deployments
- **SSL**: Let's Encrypt automated certificate management

### Deployment Overview

#### **Local Development (Docker Compose)**

```yaml
# Key services configuration
services:
  api-gateway:    # Nginx routing
  postgres:       # Multi-database setup
  document-service, authentication-service, anonymization-service
  genai-service:  # AI processing
  client:         # React SPA
  keycloak:       # Identity management
```

#### **Production Deployment (AWS + Kubernetes)**

- **Infrastructure**: AWS EC2 + Rancher Kubernetes
- **Load Balancing**: Traefik with automatic SSL
- **DNS**: Custom domains with automated certificate management
- **Scaling**: Horizontal pod autoscaling based on CPU/memory
- **Monitoring**: Prometheus metrics collection + Grafana Dashboards + Alertmanager
- **Logging**: Structured logging with log aggregation

#### **CI/CD Pipeline**

The project implements a step by step CI/CD pipeline with the following stages:

1. **Continuous Integration**

   - Automated testing for Spring Boot services
   - Python GenAI service testing with pytest
   - Docker image building and pushing to GHCR

2. **Continuous Deployment**

   - Automated deployment to AWS EC2 environment
   - Kubernetes deployment via Rancher
   - Environment-specific configuration management

3. **Post-Deployment Validation**

   - Health check verification
   - API endpoint testing
   - Service integration validation

4. **Pipeline Orchestration**
   ```yaml
   # Main workflow stages
   - test-springboot # Unit/integration tests
   - test-genai # Python service testing
   - build-and-push # Docker image creation
   - deploy-aws # EC2 deployment
   - deploy-rancher # Kubernetes deployment
   - validate-deployment # Post-deployment checks
   ```

### Security Implementation

#### **Authentication & Authorization**

- **OAuth2/JWT**: Keycloak-based identity management
- **Role-Based Access**: Service account roles with proper permissions
- **API Security**: All endpoints require valid JWT tokens
- **CORS**: Properly configured cross-origin resource sharing

#### **Data Security**

- **Encryption**: TLS/HTTPS for all communications
- **Database Security**: Isolated databases per service
- **File Storage**: Secure file handling with proper validation
- **API Key Management**: Secure environment variable handling

### Monitoring and Observability

#### **Health Monitoring**

- Spring Actuator endpoints for service health
- Custom health checks for all components
- Dependency health validation

#### **Metrics Collection**

- Prometheus metrics integration
- Custom application metrics (e.g., request latency, error rates)
- Performance monitoring

#### **Grafana Dashboards**
- Error Rate Dashboard: Tracks 4xx/5xx HTTP error rates across all services
- GenAI Latency Dashboard: Monitors response times for summarization and anonymization requests
- Traffic Summary Dashboard: Aggregates and visualizes traffic for all service endpoints

#### **Alerting**
- Prometheus alert rules for critical thresholds (e.g., high error rate, slow response time)
- AlertManager handles alert routing and notification (via email)
- **Note**: Email notifications via Gmail SMTP are currently not functional due to authentication issues — despite using an app password, Gmail rejects the credentials with a “username and password not accepted” error.

#### **Logging**

- Structured logging across all services
- Centralized log aggregation capability
- Error tracking and alerting

### Development Guidelines

#### **Code Organization**

- Microservice separation by domain
- Clean architecture patterns
- Comprehensive testing strategies
- API-first development approach

#### **Quality Assurance**

- Unit and integration testing
- Code coverage reporting
- Automated quality gates
- Documentation as code
### Monitoring Instructions 
To deploy the monitoring stack (Prometheus, Grafana, Alertmanager) via Helm:
```bash
cd helm/monitoring
```
```bash
helm upgrade --install oopsops-monitoring-app . \
  --namespace oopsops-monitoring \
  --create-namespace
```
#### **Accessing Metrics & Dashboards**
- Prometheus UI:
You can create and run custom PromQL queries directly in Prometheus: https://prometheus.monitoring.student.k8s.aet.cit.tum.de/
-  Grafana Dashboards:
Pre-configured dashboards are available in Grafana:
https://grafana.monitoring.student.k8s.aet.cit.tum.de/
The following dashboards are currently available:

**Error Rate Dashboard:**

- Shows 5xx and 4xx error rates for:

- Spring Boot services

- genai-service (FastAPI)

Prometheus queries like:

promql
```bash
rate(http_server_requests_seconds_count{status=~"5.."}[5m])
rate(http_request_duration_seconds_count{status=~"5..", job="kubernetes-genai-service"}[5m])
```
**GenAI Service Latency:**

- Shows average request latency for the GenAI service's endpoints:
```bash
/api/v1/genai/anonymize
/api/v1/genai/summarize
```
Calculated as:

```bash
rate(http_request_duration_seconds_sum{...}) / rate(http_request_duration_seconds_count{...})
```
**Traffic Summary by Service & Endpoint:**

- Visualizes total request count per service and endpoint in the last 24 hours.

- Colored bars grouped by microservice:

  1. Green → auth

  2. Orange → document

  3. Purple → anonymization

  4. Red → genai
### Troubleshooting Guide

#### **Common Issues**

1. **Port Conflicts**: Ensure all required ports are available
2. **OpenAI API**: Verify API key is properly set in environment
3. **Database Connection**: Check PostgreSQL service startup

#### **Service Dependencies**

- Keycloak must be ready before authentication service
- PostgreSQL must be available before Spring Boot services
- All services should have proper health checks
