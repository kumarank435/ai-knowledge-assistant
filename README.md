# AI Knowledge Assistant

An AI-powered document question-answering application built with **Spring Boot, React, Retrieval-Augmented Generation (RAG), ChromaDB, Ollama, and MySQL**.

The application allows authenticated users to select one or multiple PDF documents and ask natural-language questions. The system retrieves relevant document chunks using semantic search and uses a local LLM to generate grounded answers with source references.

## Features

- JWT-based authentication
- Role-based access control
  - **ADMIN**: upload and delete documents
  - **USER**: select documents and ask questions
- PDF document upload and processing
- Admin document deletion
- Multi-document selection
- Semantic vector search using ChromaDB
- Retrieval-Augmented Generation (RAG)
- Local LLM inference using Ollama
- Local embeddings using `nomic-embed-text`
- Semantic document chunking
- Conversational question answering
- Source-aware responses showing relevant chunks
- MySQL persistence for users and document metadata
- React frontend
- Backend unit and integration tests

## Architecture

```text
                    React Frontend
                         │
                         │ REST API
                         ▼
                 ┌─────────────────┐
                 │   Spring Boot   │
                 │                 │
                 │ JWT + Security  │
                 │      + RBAC     │
                 └───────┬─────────┘
                         │
              ┌──────────┴──────────┐
              │                     │
              ▼                     ▼
        ┌───────────┐         ┌─────────────┐
        │   MySQL   │         │  ChromaDB   │
        │           │         │             │
        │ Users     │         │ Embeddings  │
        │ Documents │         │ Vector      │
        │ Metadata  │         │ Search      │
        └───────────┘         └──────┬──────┘
                                     │
                                     ▼
                              ┌─────────────┐
                              │   Ollama    │
                              │             │
                              │ Embeddings  │
                              │     +       │
                              │     LLM     │
                              └─────────────┘
```

## RAG Pipeline

### Document ingestion

```text
PDF
 │
 ▼
PDF Text Extraction
 │
 ▼
Text Cleaning
 │
 ▼
Semantic Chunking
 │
 ▼
Embedding Generation
 │
 ▼
ChromaDB Vector Store
```

### Question answering

```text
User Question
      │
      ▼
Query Embedding
      │
      ▼
ChromaDB Similarity Search
      │
      ▼
Relevant Chunks
      │
      ▼
Context Construction
      │
      ▼
Ollama LLM
      │
      ▼
Grounded Answer + Sources
```

The application supports selecting multiple PDFs and restricts retrieval to the selected document IDs.

## Technology Stack

### Backend

- Java 21
- Spring Boot 4.1.1
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL 8
- Spring AI
- Maven
- Apache PDFBox

### AI / RAG

- Retrieval-Augmented Generation (RAG)
- ChromaDB
- Ollama
- `nomic-embed-text`
- `llama3.2:3b`
- Semantic text chunking
- Vector similarity search

### Frontend

- React
- Vite
- JavaScript
- CSS
- Fetch API

## Authentication & Authorization

The application uses JWT authentication and role-based authorization.

### USER

A normal user can:

- Log in
- View available documents
- Select one or multiple documents
- Ask questions about selected documents
- View retrieved sources

### ADMIN

An administrator can:

- Log in
- View documents
- Upload PDFs
- Delete documents
- Select documents
- Ask questions using RAG

Authorization is enforced by **Spring Security on the backend**. The React frontend uses the user's role to control which actions are displayed.

## Project Structure

```text
ai-knowledge-assistant/
│
├── frontend/
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
│
├── src/
│   ├── main/
│   │   ├── java/com/aiassistant/knowledge/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/com/aiassistant/knowledge/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Prerequisites

Install:

- Java 21
- Node.js and npm
- MySQL 8
- Docker
- Ollama

The project includes the Maven wrapper, so a separate Maven installation is not required.

## AI Models

Pull the required Ollama models:

```bash
ollama pull nomic-embed-text
ollama pull llama3.2:3b
```

Verify:

```bash
ollama list
```

## Database Setup

Create the MySQL database:

```sql
CREATE DATABASE ai_knowledge_assistant;
```

The application uses MySQL for user accounts and document metadata.

Do not commit database passwords, JWT secrets, API keys, or other credentials to GitHub.

## Start ChromaDB

Run ChromaDB locally using Docker.

The application expects ChromaDB at:

```text
http://localhost:8000
```

## Start Ollama

Make sure Ollama is running at:

```text
http://localhost:11434
```

## Configure the JWT Secret

The backend reads the JWT secret from the `JWT_SECRET` environment variable.

PowerShell:

```powershell
$env:JWT_SECRET="your-secure-jwt-secret"
```

Keep secrets outside the source code.

## Run the Backend

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend:

```text
http://localhost:8081
```

Run tests:

```powershell
.\mvnw.cmd clean test
```

## Run the Frontend

Open another terminal:

```powershell
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:5173
```

## API Overview

### Authentication

```text
POST /api/auth/login
POST /api/auth/register
```

### Documents

```text
GET    /api/documents
POST   /api/documents/upload
DELETE /api/documents/{id}
```

### Chat

```text
POST /api/chat
```

### Health

```text
GET /api/health
```

Protected document and chat endpoints require JWT authentication. Document upload and deletion require the ADMIN role.

## Example Chat Request

```json
{
  "question": "What are the key points in these documents?",
  "documentIds": [1, 2]
}
```

The backend retrieves relevant chunks only from the selected documents and generates the answer using the retrieved context.

## Testing

The backend contains tests for areas including:

- Application context
- Document repository
- Document service
- Document ingestion
- PDF text extraction
- Text cleaning
- Text chunking
- Embedding service
- Chroma vector store integration
- RAG service
- Chat service
- RAG evaluation

Run:

```powershell
.\mvnw.cmd clean test
```

## Security

- Passwords are stored using BCrypt hashing.
- JWT authentication protects application APIs.
- Spring Security enforces role-based authorization.
- JWT secrets are supplied through environment variables.
- Frontend role checks control UI visibility; backend authorization remains the actual security boundary.
- `.env` files and generated build directories are excluded from Git.

## Current Status

The current version includes:

- Authentication
- JWT security
- USER/ADMIN RBAC
- PDF upload
- PDF deletion
- Multi-PDF selection
- Semantic chunking
- Embeddings
- ChromaDB retrieval
- RAG-based question answering
- Source references
- React frontend
- Automated backend tests

## Future Improvements

Planned improvements include:

- Document metadata management
- Chat history
- User profile management
- Admin analytics dashboard
- Improved document processing status
- Production deployment
- Additional RAG evaluation and optimization

## Author

**Kumaran K**

B.Tech — Artificial Intelligence and Data Science

GitHub: https://github.com/kumarank435
