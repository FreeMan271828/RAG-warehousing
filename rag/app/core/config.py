"""
核心配置模块
"""
from functools import lru_cache
from typing import List, Optional
from pydantic_settings import BaseSettings
from pydantic import Field


class Settings(BaseSettings):
    """应用配置"""

    # OpenAI 配置
    openai_api_key: str = Field(default="", description="OpenAI API Key")
    openai_model: str = Field(default="gpt-3.5-turbo", description="LLM模型")
    openai_base_url: str = Field(default="https://api.openai.com/v1", description="API地址")

    # Embedding 配置
    embedding_model: str = Field(default="text-embedding-ada-002", description="向量模型")

    # ChromaDB 配置
    chroma_persist_dir: str = Field(default="./chroma_data", description="向量数据库持久化路径")

    # 知识库配置
    knowledge_path: str = Field(default="./data/knowledge", description="知识库路径")

    # 服务配置
    host: str = Field(default="0.0.0.0", description="服务地址")
    port: int = Field(default=8000, description="服务端口")
    log_level: str = Field(default="INFO", description="日志级别")
    cors_origins: str = Field(default="*", description="跨域配置")

    # API 认证
    api_key: Optional[str] = Field(default=None, description="API密钥")

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        case_sensitive = False


@lru_cache()
def get_settings() -> Settings:
    """获取配置单例"""
    return Settings()
