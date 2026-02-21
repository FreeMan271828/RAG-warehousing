"""
向量存储模块 - ChromaDB
"""
import os
from typing import List, Optional, Any
from langchain.schema import Document
from langchain_community.vectorstores import Chroma
from langchain_openai import OpenAIEmbeddings
from app.core.config import get_settings

settings = get_settings()


class VectorStoreManager:
    """向量存储管理器"""

    def __init__(self):
        self.embeddings = self._create_embeddings()
        self.vectorstore: Optional[Chroma] = None
        self._init_vectorstore()

    def _create_embeddings(self) -> OpenAIEmbeddings:
        """创建嵌入模型"""
        return OpenAIEmbeddings(
            model=settings.embedding_model,
            openai_api_key=settings.openai_api_key,
            openai_api_base=settings.openai_base_url
        )

    def _init_vectorstore(self):
        """初始化向量存储"""
        persist_directory = settings.chroma_persist_dir
        
        # 确保目录存在
        os.makedirs(persist_directory, exist_ok=True)
        
        # 如果已有数据，则加载
        if os.path.exists(os.path.join(persist_directory, "chroma.sqlite3")):
            try:
                self.vectorstore = Chroma(
                    persist_directory=persist_directory,
                    embedding_function=self.embeddings
                )
            except Exception as e:
                print(f"加载现有向量库失败: {e}")
                self.vectorstore = None

    def create_vectorstore(self, documents: List[Document]) -> Chroma:
        """从文档创建向量存储"""
        # 确保目录存在
        os.makedirs(settings.chroma_persist_dir, exist_ok=True)
        
        self.vectorstore = Chroma.from_documents(
            documents=documents,
            embedding=self.embeddings,
            persist_directory=settings.chroma_persist_dir
        )
        return self.vectorstore

    def add_documents(self, documents: List[Document]) -> None:
        """添加文档到向量存储"""
        if self.vectorstore is None:
            self.create_vectorstore(documents)
        else:
            self.vectorstore.add_documents(documents)

    def similarity_search(
        self, 
        query: str, 
        k: int = 4,
        filter: Optional[dict] = None
    ) -> List[Document]:
        """相似度搜索"""
        if self.vectorstore is None:
            return []
        return self.vectorstore.similarity_search(
            query=query,
            k=k,
            filter=filter
        )

    def similarity_search_with_score(
        self,
        query: str,
        k: int = 4,
        filter: Optional[dict] = None
    ) -> List[tuple]:
        """带分数的相似度搜索"""
        if self.vectorstore is None:
            return []
        return self.vectorstore.similarity_search_with_score(
            query=query,
            k=k,
            filter=filter
        )

    def as_retriever(self, search_kwargs: Optional[dict] = None):
        """转换为检索器"""
        if self.vectorstore is None:
            return None
        return self.vectorstore.as_retriever(
            search_kwargs=search_kwargs or {"k": 4}
        )

    def delete_collection(self) -> None:
        """删除向量集合"""
        if self.vectorstore is not None:
            self.vectorstore.delete_collection()
            self.vectorstore = None

    def get_document_count(self) -> int:
        """获取文档数量"""
        if self.vectorstore is None:
            return 0
        return self.vectorstore._collection.count()


# 全局向量存储管理器实例
vectorstore_manager = VectorStoreManager()
