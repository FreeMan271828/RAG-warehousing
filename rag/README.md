# 烟厂仓库RAG智能问答服务

基于 LangChain + FastAPI + ChromaDB 的 RAG 服务，为三维数字孪生系统提供智能问答能力。

## 技术栈

- **Python**: 3.10+
- **FastAPI**: Web框架
- **LangChain**: LLM应用框架
- **ChromaDB**: 向量数据库
- **OpenAI/OLLAMA**: 大语言模型
- **BeautifulSoup4**: 网页解析
- **PyPDF2**: PDF文档处理

## 环境要求

```bash
pip install -r requirements.txt
```

## 项目结构

```
rag/
├── app/
│   ├── api/              # API路由
│   │   └── routes.py     # API接口
│   ├── core/             # 核心配置
│   │   ├── config.py     # 配置管理
│   │   └── security.py   # 安全配置
│   ├── chains/           # LangChain链
│   │   └── rag_chain.py  # RAG问答链
│   ├── document_loaders/ # 文档加载器
│   │   ├── pdf_loader.py
│   │   ├── txt_loader.py
│   │   └── web_loader.py
│   ├── embeddings/        # 向量嵌入
│   │   └── embedding_factory.py
│   ├── vectorstores/     # 向量存储
│   │   └── chroma_store.py
│   └── main.py           # 应用入口
├── data/                 # 数据目录
│   └── knowledge/        # 知识库文档
├── chroma_data/          # ChromaDB持久化数据
├── logs/                 # 日志目录
├── requirements.txt      # 依赖
├── .env.example         # 环境变量示例
└── README.md
```

## 快速开始

### 1. 安装依赖

```bash
pip install -r requirements.txt
```

### 2. 配置环境变量

```bash
cp .env.example .env
# 编辑 .env 文件，配置 OPENAI_API_KEY 等
```

### 3. 初始化知识库

```bash
# 将文档放入 data/knowledge/ 目录
# 支持 PDF、TXT、MD 等格式

python -m app.scripts.init_knowledge
```

### 4. 启动服务

```bash
# 开发模式
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000

# 生产模式
uvicorn app.main:app --host 0.0.0.0 --port 8000 --workers 4
```

### 5. 测试

```bash
# 健康检查
curl http://localhost:8000/health

# 智能问答
curl -X POST http://localhost:8000/api/v1/rag/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "如何进行设备保养？"}'
```

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /health | 健康检查 |
| POST | /api/v1/rag/chat | 智能问答 |
| GET | /api/v1/rag/history/{conversation_id} | 获取对话历史 |
| GET | /api/v1/rag/suggestions | 获取推荐问题 |
| POST | /api/v1/rag/document | 添加文档到知识库 |
| GET | /api/v1/rag/search | 搜索知识库 |
| POST | /api/v1/rag/knowledge/init | 初始化知识库 |
| DELETE | /api/v1/rag/knowledge/clear | 清空知识库 |

## 配置说明

### 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| OPENAI_API_KEY | OpenAI API Key | - |
| OPENAI_MODEL | 使用的模型 | gpt-3.5-turbo |
| OPENAI_BASE_URL | API地址 | https://api.openai.com/v1 |
| EMBEDDING_MODEL | 向量模型 | text-embedding-ada-002 |
| CHROMA_PERSIST_DIR | 向量数据库路径 | ./chroma_data |
| KNOWLEDGE_PATH | 知识库路径 | ./data/knowledge |
| LOG_LEVEL | 日志级别 | INFO |
| CORS_ORIGINS | 允许的跨域来源 | * |

### 使用 Ollama (本地模型)

```bash
# 安装 Ollama
brew install ollama  # Mac
# 或参考官网安装

# 启动 Ollama
ollama serve
ollama pull llama2

# 配置环境变量
OLLAMA_BASE_URL=http://localhost:11434/v1
OPENAI_MODEL=llama2
OPENAI_API_KEY=ollama
```

## 知识库格式

### 文档要求

- 支持格式: PDF, TXT, MD, DOCX, HTML
- 建议每个文档一个主题
- 文档内容应包含问答案例

### 示例文档内容

```markdown
# 设备保养指南

## 如何进行设备日常保养？
1. 清洁设备表面灰尘
2. 检查设备运行状态
3. 记录保养情况

## 电池充电注意事项
1. 使用原装充电器
2. 充电时保持通风
3. 充满后及时断开电源
```

## 与Spring Boot后端对接

后端配置 `application.yml`:

```yaml
rag:
  api:
    url: http://localhost:8000
    key: your-api-key-if-needed
```

## 性能优化

1. **批量索引**: 大文档批量处理
2. **缓存**: 相似问题使用缓存
3. **异步**: 长时间任务使用异步
4. **分页**: 搜索结果分页返回

## 监控

- 日志: `logs/rag_service.log`
- Prometheus指标: `/metrics` (可选)

## 许可证

MIT License
