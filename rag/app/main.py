"""
RAG 服务主应用入口
"""
import os
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from loguru import logger
from app.core.config import get_settings
from app.api.routes import router as rag_router
from app.vectorstores.chroma_store import vectorstore_manager

settings = get_settings()


def setup_logging():
    """配置日志"""
    log_level = settings.log_level.upper()
    logger.remove()
    logger.add(
        "logs/rag_service.log",
        rotation="500 MB",
        retention="10 days",
        level=log_level,
        format="{time:YYYY-MM-DD HH:mm:ss} | {level} | {name}:{function}:{line} - {message}"
    )
    logger.add(
        lambda msg: print(msg, end=""),
        level=log_level,
        format="{time:HH:mm:ss} | {level} | {message}"
    )


def setup_cors(app: FastAPI):
    """配置跨域"""
    cors_origins = settings.cors_origins.split(",") if settings.cors_origins != "*" else ["*"]
    
    app.add_middleware(
        CORSMiddleware,
        allow_origins=cors_origins,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )


@asynccontextmanager
async def lifespan(app: FastAPI):
    """应用生命周期管理"""
    # 启动时
    logger.info("RAG 服务正在启动...")
    
    # 确保必要的目录存在
    os.makedirs("logs", exist_ok=True)
    os.makedirs(settings.chroma_persist_dir, exist_ok=True)
    os.makedirs(settings.knowledge_path, exist_ok=True)
    
    # 检查向量存储是否已初始化
    if vectorstore_manager.vectorstore is not None:
        doc_count = vectorstore_manager.get_document_count()
        logger.info(f"知识库已加载，包含 {doc_count} 个文档块")
    else:
        logger.info("知识库尚未初始化，请调用 /api/v1/rag/knowledge/init 接口")
    
    yield
    
    # 关闭时
    logger.info("RAG 服务正在关闭...")


def create_app() -> FastAPI:
    """创建应用实例"""
    setup_logging()
    
    app = FastAPI(
        title="烟厂仓库 RAG 智能问答服务",
        description="""
## 概述
基于 LangChain + ChromaDB 的 RAG 服务，为三维数字孪生系统提供智能问答能力。

## 功能
- 智能问答：基于知识库的问答
- 知识库管理：添加、搜索、初始化知识库
- 对话管理：支持多轮对话

## 使用流程
1. 首先调用 `/api/v1/rag/knowledge/init` 初始化知识库
2. 调用 `/api/v1/rag/chat` 进行智能问答
        """,
        version="1.0.0",
        docs_url="/docs",
        redoc_url="/redoc",
        lifespan=lifespan
    )
    
    # 配置跨域
    setup_cors(app)
    
    # 注册路由
    app.include_router(rag_router)
    
    @app.get("/")
    async def root():
        return {
            "message": "烟厂仓库 RAG 智能问答服务",
            "version": "1.0.0",
            "docs": "/docs"
        }
    
    @app.get("/health")
    async def health_check():
        doc_count = vectorstore_manager.get_document_count()
        return {
            "status": "healthy",
            "service": "rag-service",
            "version": "1.0.0",
            "knowledge_base": {
                "document_count": doc_count,
                "initialized": vectorstore_manager.vectorstore is not None
            }
        }
    
    return app


app = create_app()


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "app.main:app",
        host=settings.host,
        port=settings.port,
        reload=True
    )
