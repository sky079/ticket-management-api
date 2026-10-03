# Ticket Management & AI Support Assistant

A Spring Boot backend for managing support tickets with JWT
authentication, role-based access control, SLA tracking, AWS S3 file
storage, and an AI-powered Retrieval-Augmented Generation (RAG) support
assistant.

------------------------------------------------------------------------

## 1. Project Overview

The application manages the complete support-ticket lifecycle:

``` text
USER
  |
  | Create Ticket
  v
OPEN
  |
  | ADMIN assigns
  v
IN_PROGRESS
  |
  | AGENT resolves
  v
RESOLVED
```

The system also contains an AI self-service layer. Before creating a
support ticket, a user can ask the AI assistant for help.

The AI searches:

-   Knowledge-base articles
-   Previously resolved tickets

and uses the retrieved information to generate a practical answer.

------------------------------------------------------------------------

# 2. Main Technologies

-   Java 17
-   Spring Boot 4
-   Spring Security
-   JWT
-   Spring Data JPA / Hibernate
-   PostgreSQL
-   pgvector
-   Ollama
-   `nomic-embed-text`
-   Qwen 2.5 3B
-   AWS S3
-   AWS Systems Manager Parameter Store
-   Docker
-   Maven
-   Swagger / OpenAPI

------------------------------------------------------------------------

# 3. Application Architecture

``` text
                         CLIENT
                           |
                           v
                    SPRING BOOT API
                           |
        +------------------+------------------+
        |                  |                  |
        v                  v                  v
   AUTHENTICATION      TICKET SYSTEM      AI ASSISTANT
        |                  |                  |
        v                  v                  v
       JWT             PostgreSQL            RAG
                           |                  |
                           |          +-------+-------+
                           |          |               |
                           |          v               v
                           |     KNOWLEDGE        RESOLVED
                           |      ARTICLES          TICKETS
                           |          |               |
                           |          +-------+-------+
                           |                  |
                           |                  v
                           |              PGVECTOR
                           |                  |
                           |                  v
                           |              OLLAMA
                           |                  |
                           |                  v
                           |             AI RESPONSE
                           |
                           +------ AWS S3
                           |
                           +------ AWS SSM
```

------------------------------------------------------------------------

# 4. Package Structure

The project follows a layer-first package structure.

``` text
com.ticket.ticketmanagement

├── controller/
│   ├── TicketController.java
│   ├── AssistantController.java
│   ├── KnowledgeArticleController.java
│   ├── UserController.java
│   └── AuthController.java
│
├── service/
│   ├── TicketService.java
│   ├── TicketChunkService.java
│   ├── RagService.java
│   ├── RagPromptService.java
│   ├── KnowledgeArticleService.java
│   ├── KnowledgeChunkService.java
│   ├── UserService.java
│   ├── AuthService.java
│   ├── S3Service.java
│   └── ParameterStoreService.java
│
├── repository/
│   ├── TicketRepository.java
│   ├── TicketChunkRepository.java
│   ├── KnowledgeArticleRepository.java
│   ├── KnowledgeChunkRepository.java
│   └── UserRepository.java
│
├── entity/
│   ├── Ticket.java
│   ├── TicketChunk.java
│   ├── KnowledgeArticle.java
│   ├── KnowledgeChunk.java
│   └── User.java
│
├── dto/
│   ├── Ticket DTOs
│   ├── RAG DTOs
│   ├── Knowledge DTOs
│   ├── User DTOs
│   └── Auth DTOs
│
├── ai/
│   ├── EmbeddingService.java
│   └── LlmService.java
│
├── config/
│   ├── S3Config.java
│   ├── SsmConfig.java
│   ├── S3Properties.java
│   └── JacksonConfig.java
│
├── security/
├── validation/
├── scheduler/
├── exception/
└── TicketManagementApplication.java
```

------------------------------------------------------------------------

# 5. Authentication and Authorization

The application uses JWT-based authentication.

Supported roles:

``` text
USER
AGENT
ADMIN
```

The basic permission model is:

``` text
USER
 ├── Create tickets
 └── Ask AI assistant

AGENT
 ├── Work on assigned tickets
 └── Resolve tickets

ADMIN
 ├── Assign tickets
 ├── Resolve tickets
 └── View reports
```

A successful login returns a JWT token.

Protected requests use:

``` http
Authorization: Bearer <JWT_TOKEN>
```

------------------------------------------------------------------------

# 6. Ticket Management

## Ticket Creation

A user creates a ticket with information such as:

-   Title
-   Description
-   Priority
-   Optional attachment

The ticket starts in:

``` text
OPEN
```

------------------------------------------------------------------------

## Ticket Assignment

An ADMIN assigns the ticket to an AGENT.

The ticket changes to:

``` text
IN_PROGRESS
```

------------------------------------------------------------------------

## Ticket Resolution

An AGENT or ADMIN can resolve the ticket.

The ticket stores:

-   Resolution
-   Resolved timestamp
-   SLA status

The ticket changes to:

``` text
RESOLVED
```

------------------------------------------------------------------------

# 7. SLA Management

The system calculates SLA status when a ticket is resolved.

Supported priority levels:

``` text
HIGH
MEDIUM
LOW
```

The corresponding SLA limits are configured through the application's
SLA configuration.

Possible SLA states:

``` text
PENDING
MET
BREACHED
```

Conceptually:

``` text
Ticket Created
      |
      v
  SLA Starts
      |
      v
Ticket Resolved
      |
      v
Calculate Resolution Time
      |
      v
Compare With SLA Policy
      |
      +---------+---------+
      |                   |
      v                   v
     MET               BREACHED
```

------------------------------------------------------------------------

# 8. AWS S3 File Storage

Ticket attachments are stored in AWS S3.

The flow is:

``` text
CLIENT
  |
  | Multipart File
  v
Ticket Controller
  |
  v
Ticket Service
  |
  v
S3Service
  |
  v
AWS S3
  |
  v
Attachment URL
  |
  v
Ticket
```

A unique file name is generated before uploading the file.

------------------------------------------------------------------------

# 9. AWS Parameter Store

AWS Systems Manager Parameter Store is used for application
configuration that should not be hard-coded.

The S3 bucket name is retrieved from:

``` text
/ticket-management/aws/bucket-name
```

Flow:

``` text
Application
     |
     v
ParameterStoreService
     |
     v
AWS Systems Manager
Parameter Store
     |
     v
Parameter Value
```

AWS credentials are supplied through the AWS SDK credential provider
mechanism rather than being hard-coded in source code.

------------------------------------------------------------------------

# 10. AI Self-Service

The application contains an AI assistant that can attempt to solve a
user's problem before the user creates a support ticket.

The current flow is:

``` text
USER
  |
  | Ask Question
  v
AI ASSISTANT
  |
  v
RAG SEARCH
  |
  +----------------------+
  |                      |
  v                      v
KNOWLEDGE ARTICLES    RESOLVED TICKETS
  |                      |
  +----------+-----------+
             |
             v
      RELEVANT CONTEXT
             |
             v
          OLLAMA
             |
             v
        AI RESPONSE
             |
             v
       ANSWER + SOURCES
```

The current implementation ends after returning the AI answer and
sources.

The following feedback flow is a planned future enhancement:

``` text
AI RESPONSE
    |
    v
Did it solve the problem?
    |
    +----------+----------+
    |                     |
   YES                    NO
    |                     |
    v                     v
Store AI             Create Ticket
resolution               |
                          v
                    Existing Ticket
                       Workflow
```

This feedback/escalation flow is not part of the current implementation.

------------------------------------------------------------------------

# 11. What is RAG?

RAG stands for:

``` text
Retrieval-Augmented Generation
```

RAG is not a single AI model.

It is a process that combines:

``` text
Retrieval
    +
Generation
```

The application first retrieves relevant information from its own data
and then gives that information to an LLM to generate the answer.

------------------------------------------------------------------------

# 12. Why RAG?

A normal LLM can answer using information learned during model training.

However, the ticket management application contains its own internal
information:

-   Company troubleshooting instructions
-   Knowledge articles
-   Previous support tickets
-   Agent resolutions

That information is not necessarily part of the LLM's training data.

RAG allows the application to provide this internal information to the
model at request time.

------------------------------------------------------------------------

# 13. RAG Data Sources

The application currently uses two sources.

## Knowledge Articles

Knowledge articles contain reusable support information.

Example:

``` text
Title:
VPN Connection Troubleshooting

Category:
VPN

Content:
Remove saved VPN credentials and reconnect using
the new password.
```

------------------------------------------------------------------------

## Resolved Tickets

Resolved tickets contain real support problems and their solutions.

Example:

``` text
Title:
VPN authentication failed after password reset

Description:
User cannot connect to the company VPN after changing
their password.

Resolution:
Removed the old VPN credentials, restarted the VPN client,
and configured the new password.
```

A resolved ticket therefore becomes reusable knowledge.

------------------------------------------------------------------------

# 14. Knowledge Article Flow

When a knowledge article is created:

``` text
Knowledge Article
       |
       v
   Chunk Content
       |
       v
EmbeddingService
       |
       v
Generate Vector
       |
       v
KnowledgeChunk
       |
       v
PostgreSQL + pgvector
```

------------------------------------------------------------------------

# 15. Resolved Ticket Knowledge Flow

When a ticket is resolved:

``` text
Ticket
  |
  v
RESOLVED
  |
  v
Ticket Resolution
  |
  v
TicketChunkService
  |
  v
Create Chunks
  |
  v
EmbeddingService
  |
  v
Generate Embeddings
  |
  v
PostgreSQL + pgvector
```

This means the system improves its searchable knowledge base naturally
as agents resolve tickets.

No model retraining is required for every new ticket.

------------------------------------------------------------------------

# 16. Chunking

Chunking means dividing large text into smaller pieces.

For example:

``` text
Large Document
      |
      +---- Chunk 0
      |
      +---- Chunk 1
      |
      +---- Chunk 2
```

The current implementation uses a fixed chunk size.

Each chunk contains:

``` text
source
content
chunkIndex
embedding
```

The reason for chunking is that RAG normally retrieves the relevant
section rather than sending an entire large document to the LLM.

------------------------------------------------------------------------

# 17. Embeddings

An embedding represents text as a vector of numbers.

The application uses:

``` text
nomic-embed-text
```

The model produces a:

``` text
768-dimensional vector
```

Example:

``` text
"VPN is not connecting"
          |
          v
  nomic-embed-text
          |
          v
[0.021, -0.18, 0.44, ...]
```

Semantically similar text produces vectors that are closer to each other
in vector space.

------------------------------------------------------------------------

# 18. pgvector

PostgreSQL is used as both:

-   The application's relational database
-   The vector database

The `pgvector` extension allows PostgreSQL to store vector embeddings.

Example:

``` sql
vector(768)
```

The application performs similarity search using pgvector.

The current query uses:

``` sql
ORDER BY embedding <=> CAST(:embedding AS vector)
```

The `<=>` operator is used for cosine distance.

------------------------------------------------------------------------

# 19. Semantic Search

Traditional keyword search might look for exact words.

For example:

``` text
"VPN password problem"
```

RAG semantic search can retrieve related text even when the wording is
different.

Example:

``` text
Question:
VPN stopped working after password reset

Stored resolution:
Old VPN credentials were removed and the
new password was configured.
```

The wording is different, but the meaning is related.

The embedding model helps identify this semantic relationship.

------------------------------------------------------------------------

# 20. RAG Search Process

When the user asks:

``` text
VPN is not connecting after changing my password.
```

the application performs:

``` text
User Question
      |
      v
EmbeddingService
      |
      v
Question Embedding
      |
      +--------------------------+
      |                          |
      v                          v
KnowledgeChunkRepository    TicketChunkRepository
      |                          |
      v                          v
Similar Knowledge Chunks    Similar Ticket Chunks
      |                          |
      +------------+-------------+
                   |
                   v
             RAG Search Results
                   |
                   v
             RagPromptService
                   |
                   v
              RAG Prompt
                   |
                   v
                LlmService
                   |
                   v
               Ollama LLM
                   |
                   v
              Final Answer
```

------------------------------------------------------------------------

# 21. Two AI Models

The system uses two different models.

## Embedding Model

``` text
nomic-embed-text
```

Purpose:

``` text
Text -> Vector
```

Used for retrieval.

------------------------------------------------------------------------

## Generation Model

``` text
qwen2.5:3b
```

Purpose:

``` text
Context + Question -> Answer
```

Used for generation.

------------------------------------------------------------------------

# 22. Ollama

Ollama runs the AI models locally.

The Spring Boot application communicates with Ollama over HTTP.

Embedding request:

``` text
Spring Boot
    |
    v
Ollama
    |
    v
nomic-embed-text
    |
    v
Embedding
```

Generation request:

``` text
Spring Boot
    |
    v
Ollama
    |
    v
qwen2.5:3b
    |
    v
Generated Answer
```

------------------------------------------------------------------------

# 23. RAG Prompt

After retrieving relevant chunks, the application builds a prompt
containing:

-   Source type
-   Source ID
-   Title
-   Retrieved content
-   User question

The prompt instructs the LLM to:

-   Use the provided knowledge
-   Avoid inventing information
-   Use both knowledge articles and resolved tickets
-   State when the information is insufficient
-   Provide clear and practical steps

Conceptually:

``` text
You are a support assistant.

Knowledge:

Source Type: KNOWLEDGE_ARTICLE
Title: VPN Connection Troubleshooting
Content: ...

Source Type: RESOLVED_TICKET
Title: VPN authentication failed after password reset
Content: ...

User Question:
VPN is not connecting after changing my password.

Answer:
```

------------------------------------------------------------------------

# 24. AI Response

The assistant returns:

-   Generated answer
-   Source information

Example:

``` json
{
  "answer": "Remove the saved VPN credentials, restart the VPN application, and enter your new password.",
  "sources": [
    {
      "sourceType": "KNOWLEDGE_ARTICLE",
      "sourceId": 3,
      "title": "VPN Connection Troubleshooting"
    },
    {
      "sourceType": "RESOLVED_TICKET",
      "sourceId": 1,
      "title": "VPN authentication failed after password reset"
    }
  ]
}
```

Sources allow the application to show where the answer came from.

------------------------------------------------------------------------

# 25. Important RAG Concept

The system does not retrain the LLM every time a ticket is resolved.

Instead:

``` text
New Resolution
      |
      v
Chunk
      |
      v
Embedding
      |
      v
pgvector
      |
      v
Available for Future Retrieval
```

This is one of the main advantages of RAG.

The knowledge can change without retraining the generation model.

------------------------------------------------------------------------

# 26. AI Assistant API

## Ask a Question

``` http
POST /api/assistant/ask?question=VPN%20is%20not%20connecting
```

Processing:

``` text
Question
   |
   v
Generate Question Embedding
   |
   v
Search Knowledge Chunks
   |
   v
Search Ticket Chunks
   |
   v
Combine Results
   |
   v
Build Prompt
   |
   v
Call Ollama
   |
   v
Return Answer + Sources
```

------------------------------------------------------------------------

# 27. Knowledge APIs

## Create Knowledge Article

``` http
POST /api/knowledge
```

Creates an article and generates its chunks and embeddings.

------------------------------------------------------------------------

## Get All Articles

``` http
GET /api/knowledge
```

------------------------------------------------------------------------

## Get Article

``` http
GET /api/knowledge/{id}
```

------------------------------------------------------------------------

## Delete Article

``` http
DELETE /api/knowledge/{id}
```

------------------------------------------------------------------------

## Test Embedding

``` http
GET /api/knowledge/embedding-test?text=...
```

------------------------------------------------------------------------

## Test Semantic Search

``` http
GET /api/knowledge/search-test?question=...
```

------------------------------------------------------------------------

# 28. Ticket APIs

## Create Ticket

``` http
POST /api/tickets
```

------------------------------------------------------------------------

## Get Tickets

``` http
GET /api/tickets?page=0&size=10
```

------------------------------------------------------------------------

## Get Ticket

``` http
GET /api/tickets/{id}
```

------------------------------------------------------------------------

## Assign Ticket

``` http
PUT /api/tickets/{id}/assign?agentId={agentId}
```

ADMIN only.

------------------------------------------------------------------------

## Resolve Ticket

``` http
PUT /api/tickets/{id}/resolve
```

AGENT or ADMIN.

Resolving a ticket triggers:

``` text
Resolution
    |
    +--> SLA Calculation
    |
    +--> Ticket Chunking
    |
    +--> Embedding Generation
    |
    +--> pgvector Storage
```

------------------------------------------------------------------------

# 29. Database

Main application tables include:

``` text
users
tickets
knowledge_articles
knowledge_chunks
ticket_chunks
sla_policies
```

RAG vector tables contain:

``` text
knowledge_chunks
ticket_chunks
```

Each vector is:

``` text
vector(768)
```

------------------------------------------------------------------------

# 30. Docker Infrastructure

Docker is used for PostgreSQL and Ollama.

``` text
+-----------------------+
| Docker                |
|                       |
|  PostgreSQL           |
|  + pgvector           |
|                       |
|  Ollama                |
|  + embedding model    |
|  + generation model   |
+-----------------------+
```

Start the infrastructure:

``` bash
docker compose up -d
```

Check containers:

``` bash
docker ps
```

------------------------------------------------------------------------

# 31. Ollama Setup

Pull the embedding model:

``` bash
docker exec -it ticket_ollama ollama pull nomic-embed-text
```

Pull the generation model:

``` bash
docker exec -it ticket_ollama ollama pull qwen2.5:3b
```

Check models:

``` bash
docker exec -it ticket_ollama ollama list
```

------------------------------------------------------------------------

# 32. PostgreSQL + pgvector Setup

The PostgreSQL container uses a pgvector-enabled image.

The vector extension must be enabled:

``` sql
CREATE EXTENSION IF NOT EXISTS vector;
```

The RAG tables contain vector columns such as:

``` sql
vector(768)
```

------------------------------------------------------------------------

# 33. Running the Application

Prerequisites:

-   Java 17
-   Maven
-   Docker
-   AWS account/configuration
-   S3 bucket
-   SSM Parameter Store
-   Docker Desktop

Start infrastructure:

``` bash
docker compose up -d
```

Then start Spring Boot:

``` bash
mvn spring-boot:run
```

Or run the application from IntelliJ IDEA.

------------------------------------------------------------------------

# 34. Swagger

Swagger UI is available at:

``` text
http://localhost:8080/swagger-ui.html
```

Authenticate using the JWT obtained from the login endpoint.

Use:

``` text
Bearer <JWT_TOKEN>
```

------------------------------------------------------------------------

# 35. Complete End-to-End Flow

The complete application flow is:

``` text
                         USER
                           |
             +-------------+-------------+
             |                           |
             v                           v
        CREATE TICKET               ASK AI
             |                           |
             v                           v
           OPEN                    RAG SEARCH
             |                     /         \
             |                    /           \
             |                   v             v
             |             Knowledge       Resolved
             |              Articles        Tickets
             |                   \             /
             |                    \           /
             |                     v         v
             |                    Relevant Context
             |                          |
             |                          v
             |                       Ollama
             |                          |
             |                          v
             |                     AI Answer
             |                          |
             |                          v
             |                    Answer + Sources
             |
             v
           ADMIN
             |
             v
          ASSIGN
             |
             v
        IN_PROGRESS
             |
             v
          AGENT
             |
             v
          RESOLVE
             |
       +-----+------+
       |            |
       v            v
  SLA Calculation  Resolution
                    |
                    v
               Ticket Chunk
                    |
                    v
                Embedding
                    |
                    v
                 pgvector
                    |
                    v
             Future RAG Search
```

------------------------------------------------------------------------

# 36. Example Scenario

### Step 1 --- User has a problem

``` text
VPN is not connecting after changing my password.
```

### Step 2 --- User asks AI

``` http
POST /api/assistant/ask
```

### Step 3 --- Question is embedded

``` text
Question
   |
   v
nomic-embed-text
   |
   v
768-dimensional vector
```

### Step 4 --- Database search

The vector is compared against:

``` text
knowledge_chunks
ticket_chunks
```

### Step 5 --- Relevant information is retrieved

For example:

``` text
Knowledge Article:
VPN Connection Troubleshooting
```

and:

``` text
Resolved Ticket:
VPN authentication failed after password reset
```

### Step 6 --- LLM generates answer

The retrieved content is passed to:

``` text
qwen2.5:3b
```

### Step 7 --- User receives answer

``` text
Remove the saved VPN credentials,
restart the VPN client, and configure
the new password.
```

The response also contains the retrieved sources.

------------------------------------------------------------------------

# 37. Current Scope

The current implementation includes:

-   Authentication
-   JWT security
-   Role-based authorization
-   Ticket creation
-   Ticket assignment
-   Ticket resolution
-   SLA calculation
-   AWS S3 attachments
-   AWS Parameter Store
-   Knowledge articles
-   Knowledge chunks
-   Resolved ticket chunks
-   Embeddings
-   pgvector semantic search
-   Ollama integration
-   RAG prompt generation
-   AI-generated support answers
-   AI response sources

The AI assistant currently provides the answer and sources.

The optional user-feedback flow:

``` text
Did this solve the problem?
      |
      +--> YES -> store AI resolution
      |
      +--> NO  -> redirect to Create Ticket
```

is intentionally left as a future enhancement.

------------------------------------------------------------------------

# 38. Future Improvements

Potential future improvements include:

-   AI resolution confirmation
-   "Did this solve your problem?" feedback
-   Store confirmed AI resolutions
-   Redirect to ticket creation when AI cannot solve an issue
-   Similarity threshold
-   Better sentence/paragraph-based chunking
-   Chunk overlap
-   Context deduplication
-   Re-embedding when articles are updated
-   Delete chunks when source records are deleted
-   Transactions around source and chunk creation
-   Externalize Ollama configuration
-   Better Ollama error handling
-   RAG evaluation
-   RAG monitoring
-   pgvector indexes for larger datasets
-   Unit and integration tests

------------------------------------------------------------------------

# 39. Project Goal

The project demonstrates how a traditional support-ticket backend can be
extended with an AI-powered self-service layer.

The important architecture is:

``` text
Traditional Support System
          +
Knowledge Base
          +
Resolved Ticket History
          +
Vector Search
          +
LLM
          =
AI-Assisted Support System
```

The system combines conventional backend engineering with:

-   REST APIs
-   Authentication
-   Authorization
-   Relational databases
-   Cloud storage
-   Vector databases
-   Embeddings
-   Semantic search
-   Retrieval-Augmented Generation
-   Local LLM inference
-   AI-assisted support
