# Spring AI Playground (RAG + OpenAI + Tools)

**Spring AI**를 활용하여 **RAG (Retrieval-Augmented Generation)** 시스템과 **OpenAI Chat** 기능을 구현한 학습용 프로젝트입니다.  
PDF 문서를 벡터 임베딩으로 변환하여 저장하고, 이를 기반으로 LLM과 맥락 있는 대화를 나눌 수 있습니다.

## 🛠 실행 환경 (Execution Environment)

이 프로젝트를 실행하기 위해 필요한 최소 사양 및 도구입니다.

- **Java**: JDK 17+
- **Framework**: Spring Boot 3.5.3 (Kotlin 1.9.25)
- **Database**: PostgreSQL 15+ (with `pgvector` extension)
- **Container**: Docker (데이터베이스 실행용)
- **Build Tool**: Gradle (Wrapper 포함)
- **External API**: 
  - **OpenAI API Key** (필수)
  - **Tavily API Key** (선택 - 웹 검색 도구 사용 시)

---

## 📂 프로젝트 구조 (Project Structure)

주요 소스 코드는 `src/main/kotlin/com/hanyoonsoo/springaiplayground` 하위에 위치합니다.

```
springaiplayground
├── chat        # 일반적인 OpenAI 채팅 기능 (Simple Chat)
├── global      # 전역 설정 및 유틸리티
│   ├── config  # Spring AI, PGVector, Swagger 등 설정
│   └── tool    # Function Calling을 위한 커스텀 도구 (예: TavilySearch)
├── openai      # OpenAI 서비스 계층 (ChatClient, Streaming 등)
├── rag         # RAG (검색 증강 생성) 핵심 비즈니스 로직
│   ├── controller # 문서 업로드 및 검색/채팅 API
│   ├── service    # 문서 파싱, 임베딩, 질의응답 로직
│   ├── repository # Vector Store (PGVector) 연동
│   └── prompt     # LLM 프롬프트 템플릿 관리
└── project     # 프로젝트/토픽 관리 도메인
```

---

## 🚀 주요 기능 (Features)

### 1. 프로젝트 관리 (Project Management)
- **멀티 프로젝트 지원**: 여러 개의 RAG 프로젝트를 독립적으로 생성하고 관리할 수 있습니다.
- **데이터 격리**: 각 문서는 특정 프로젝트에 속하며, 검색 및 채팅 시 해당 프로젝트의 데이터만 참조합니다.

### 2. RAG (파인튜닝 없는 지식 주입)
- **PDF 문서 업로드**: PDF 파일을 업로드하면 텍스트를 추출하고 의미 단위로 분할(Token/Text Splitter)합니다.
- **임베딩 저장**: 분할된 텍스트를 OpenAI Embedding 모델을 통해 벡터로 변환하여 PostgreSQL(pgvector)에 저장합니다.
- **유사도 검색**: 사용자의 질문과 의미적으로 가장 가까운 문서 조각을 검색합니다.

### 3. Chat with Tools
- **Function Calling**: LLM이 대화 도중 실시간 정보가 필요할 때 외부 도구(예: 웹 검색)를 호출하도록 설정되어 있습니다.
- *(참고: 현재 Tavily Search 도구는 설정(`Config`)에 포함되어 있으나, 메인 RAG 흐름에서의 활성화 여부는 `RagService` 로직에 따릅니다.)*

### 4. Vector Store
- **PostgreSQL + pgvector**: 관계형 데이터베이스의 안정성과 벡터 검색 기능을 동시에 활용합니다.
- **Metadata Filtering**: 문서 검색 시 프로젝트 ID 등 메타데이터를 기반으로 필터링을 지원합니다.

---

## 🏃‍♂️ 시작하기 (Getting Started)

### 1. 환경 변수 설정
`src/main/resources/application.yml`에서 참조하는 환경 변수를 설정해야 합니다. 터미널 환경 변수나 IDE 실행 설정에 추가해주세요.

```bash
export OPENAI_API_KEY="sk-..."
export TAVILY_API_KEY="tvly-..." # (Optional)
export DB_USERNAME="postgres"
export DB_PASSWORD="password"
```

### 2. 데이터베이스 실행 (Docker)
Docker를 사용하여 pgvector가 설치된 PostgreSQL 컨테이너를 실행합니다.

```bash
# 프로젝트 루트에서 실행
docker-compose -f docker-compose.yml up -d
```

### 3. 애플리케이션 실행
Gradle Wrapper를 사용하여 서버를 띄웁니다.

```bash
./gradlew bootRun
```

---

## 📖 API 사용법 (API Usage)

서버가 실행되면 `http://localhost:8080/swagger-ui/index.html`에서 Swagger UI를 확인할 수 있습니다.

### 1. 프로젝트 관리 (Project Management)
RAG 작업을 수행하기 전에 프로젝트를 생성해야 합니다.

**프로젝트 생성**
- **URL**: `POST /api/v1/projects`
```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -d '{ "projectName": "My First RAG Project" }'
```

**프로젝트 목록 조회**
- **URL**: `GET /api/v1/projects`
```bash
curl "http://localhost:8080/api/v1/projects"
```

### 2. 문서 업로드 (Upload)
RAG에 사용할 지식(PDF)을 업로드합니다.
- **URL**: `POST /api/v1/rag/projects/{projectId}/documents`
```bash
curl -X POST http://localhost:8080/api/v1/rag/projects/1/documents \
  -F "file=@/path/to/manual.pdf"
```

### 3. 유사도 검색 (Similarity Search)
저장된 문서 중에서 질문과 관련된 내용을 검색합니다 (채팅 전 테스트용).
- **URL**: `GET /api/v1/rag/projects/{projectId}/search`
```bash
curl "http://localhost:8080/api/v1/rag/projects/1/search?query=이+프로젝트의+목적은?"
```

### 4. 채팅 (Chat)
검색된 문서를 맥락(Context)으로 삼아 AI와 대화합니다.
- **URL**: `POST /api/v1/rag/projects/{projectId}/chat`
```bash
curl -X POST http://localhost:8080/api/v1/rag/projects/1/chat \
  -H "Content-Type: application/json" \
  -d '{ "query": "업로드한 문서를 요약해줘" }'
```
