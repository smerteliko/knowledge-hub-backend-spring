
-----

# 📚 Knowledge Hub: Personal Knowledge System (Backend)

## ✨ Overview

Knowledge Hub is a full-featured **"Read-it-Later" / "Knowledge Management"** system designed for structuring and searching personal notes, articles, and links. This project serves as a demonstration of robust, end-to-end integration using modern Java/Spring technologies and NoSQL search infrastructures.

### Key Features

* **Authentication:** Secured API access using **JWT** (JSON Web Tokens).
* **Hybrid Storage:** **PostgreSQL** for relational data (CRUD, relationships) and **Elasticsearch** for high-performance full-text search.
* **Auto-Parsing:** Extracts metadata (`title`, `description`, `og:tags`) from provided URLs using **Jsoup**.
* **Full-Text Search:** Provides instant, weighted search across note content, titles, and link descriptions via Elasticsearch.
* **Export:** Generates user knowledge reports in **PDF** (Apache PDFBox) and **Markdown** formats.

### Tech Stack

| Category | Technology | Notes |
| :--- | :--- | :--- |
| **Backend** | Spring Boot 3 (Java 17+) | Core framework for REST API development. |
| **Persistence** | Spring Data JPA / Hibernate | ORM for PostgreSQL. |
| **Database** | PostgreSQL | Primary relational data store. |
| **Search** | Spring Data Elasticsearch (7.17) | Search engine for indexing and retrieval. |
| **Security** | Spring Security / JWT | Token-based authentication and authorization. |
| **Tools** | Testcontainers, Jsoup, Apache PDFBox | Testing isolation and file handling. |
| **Tests** | JUnit 5, Mockito, Awaitility | Comprehensive integration and unit testing. |

## ⚙️ Project Setup

The project is fully containerized using Docker Compose for easy deployment of the entire stack (PostgreSQL, Elasticsearch, Spring App).

### Requirements

* **Docker** and **Docker Compose**
* **Java 25**
* **Maven 3+**

### Instructions

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/smerteliko/knowledge-hub-backend-spring.git
    cd knowledge-hub-backend-spring
    ```

2.  **Build the project:**

    ```bash
    mvn clean install -DskipTests 
    ```

3.  **Start the stack (PostgreSQL, Elasticsearch, and Spring App):**

    ```bash
    docker compose up --build -d
    ```

    The application will be accessible at `http://localhost:8080`.

## 🧪 Testing

The project includes a robust set of Unit and Integration Tests using **Testcontainers** to ensure a clean and isolated environment for the PostgreSQL and Elasticsearch components.

**Run Tests:**

```bash
    mvn test
```

-----

# 🌐 Frontend Application

The user interface for the Knowledge Hub is being developed as a separate Vue.js 3 Single-Page Application (SPA).

**Frontend Repository (In Development):** **[https://github.com/smerteliko/knowledge-hub-frontend-vue](https://github.com/smerteliko/knowledge-hub-frontend-vue)**

-----

## 🤝 Contributing

I welcome any suggestions and feedback for improvement. If you find a bug or want to propose a new feature, please create a **GitHub Issue** or a **Pull Request**.

-----

## 👨‍💻 Contact

* **Name:** Nikolay Makarov
* **GitHub:** https://github.com/smerteliko
* **LinkedIn:** https://www.linkedin.com/in/nikolay-makarov/