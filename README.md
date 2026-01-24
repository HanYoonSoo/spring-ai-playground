# Spring AI Playground (RAG + OpenAI + Tools)

이 프로젝트는 **Spring AI**를 활용하여 **RAG (Retrieval-Augmented Generation)** 시스템과 **OpenAI Chat** 기능을 구현한 예제 애플리케이션입니다.
문서 업로드, 임베딩 저장(PostgreSQL/pgvector), 유사도 검색, 그리고 LLM을 이용한 질의응답 기능을 제공합니다.

## 🛠 Tech Stack

- **Language**: Kotlin (JDK 17)
- **Framework**: Spring Boot 3.x
- **AI/ML**: Spring AI (1.0.0), OpenAI API
- **Database**: PostgreSQL (with [pgvector](https://github.com/pgvector/pgvector) extension)
- **Tools**: PDFBox (PDF 파싱), Tavily Search (Web Search - *Optional*)
- **Build Tool**: Gradle

## 🚀 Features

1.  **PDF 문서 업로드 & 임베딩 (RAG)**
    - PDF 파일을 업로드하면 텍스트를 추출하고 청킹(Chunking)하여 Vector Store에 저장합니다.
    - 문서 요약본을 별도로 생성하여 저장하는 Summary Embedding 전략을 포함합니다.
2.  **유사도 검색 (Similarity Search)**
    - 사용자의 질문과 가장 유사한 문서 조각을 Vector DB에서 검색합니다.
3.  **검색 기반 채팅 (Chat with RAG)**
    - 검색된 문서를 컨텍스트로 포함하여 OpenAI GPT 모델에게 질문합니다.
    - (옵션) Tavily Search Tool을 연동하여 웹 검색 결과를 답변에 활용할 수 있습니다.
4.  **Custom Tools**
    - `@Bean` 기반의 Function Calling 구현 예시 (`TavilySearchConfig`).

## 📋 Prerequisites

- **Java 17** 이상
- **Docker** (PostgreSQL/pgvector 실행용)
- **OpenAI API Key**
- **Tavily API Key** (Web Search 사용 시)

## ⚙️ Configuration

`src/main/resources/application.yml` 파일을 참고하여 환경 변수를 설정해야 합니다.

### Environment Variables
| Variable Name | Description | Example |
|---|---|---|
| `OPENAI_API_KEY` | OpenAI API Key | `sk-proj-...` |
| `TAVILY_API_KEY` | Tavily Search API Key | `tvly-...` |
| `DB_USERNAME` | PostgreSQL Username | `postgres` |
| `DB_PASSWORD` | PostgreSQL Password | `password` |

### `application.yml` 예시
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
    tavily:
      api-key: ${TAVILY_API_KEY}
  datasource:
    url: jdbc:postgresql://localhost:5432/spring_ai_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

## 🏃‍♂️ How to Run

### 1. Database Setup (Docker)
PostgreSQL과 pgvector 확장 기능이 포함된 이미지를 실행합니다.
```bash
docker run -d \
  --name spring-ai-pgvector \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=spring_ai_db \
  -p 5432:5432 \
  pgvector/pgvector:pg16
```

### 2. Application Run
```bash
./gradlew bootRun
```

## 📖 API Usage

### 1. 문서 업로드 (Upload PDF)
PDF 문서를 업로드하여 벡터 DB에 저장합니다.
```bash
curl -X POST http://localhost:8080/api/v1/rag/projects/1/documents \
  -F "file=@/path/to/my-document.pdf"
```

### 2. 유사 문서 검색 (Search Documents)
질문과 관련된 문서 조각을 검색합니다.
```bash
curl "http://localhost:8080/api/v1/rag/projects/1/search?query=RAG란 무엇인가?"
```

### 3. 채팅 (Chat with RAG)
RAG를 활용하여 질문에 답변합니다.
```bash
curl -X POST http://localhost:8080/api/v1/rag/projects/1/chat \
  -H "Content-Type: application/json" \
  -d '{ "query": "업로드한 문서를 바탕으로 RAG에 대해 설명해줘" }'
```

## 📂 Project Structure

```
src/main/kotlin/com/hanyoonsoo/springaiplayground
├── chat        # 일반 채팅 관련 로직
├── global      # 전역 설정 (Config, Tools, Exception)
│   ├── config
│   │   ├── TavilySearchConfig.kt  # Tavily 툴 설정 (@Bean 방식)
│   │   └── VectorStoreConfig.kt   # PGVector 설정
│   └── tool    # Custom Tools (TavilySearchTool - @Tool 방식)
├── openai      # OpenAI 연동 서비스
│   └── service
│       └── OpenAiService.kt       # ChatClient 및 Function Calling 처리
├── rag         # RAG 핵심 로직
│   ├── controller
│   │   └── RagController.kt       # API 엔드포인트
│   ├── service
│   │   └── RagService.kt          # 문서 처리, 검색, 채팅 비즈니스 로직
│   ├── repository                 # Vector Store 접근
│   └── entity                     # JPA Entity (참고용)
└── project     # 프로젝트 관리 도메인
```
