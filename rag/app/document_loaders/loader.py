"""
文档加载器模块
支持 PDF、TXT、Markdown、DOCX、HTML 等格式
"""
import os
from typing import List, Optional
from pathlib import Path
from langchain.schema import Document
from langchain_community.document_loaders import (
    PyPDFLoader,
    TextLoader,
    UnstructuredMarkdownLoader,
    UnstructuredWordDocumentLoader,
    BSHTMLLoader,
    DirectoryLoader,
)
from langchain.text_splitter import (
    RecursiveCharacterTextSplitter,
    MarkdownHeaderTextSplitter
)
from app.core.config import get_settings

settings = get_settings()


class DocumentLoader:
    """文档加载器"""

    def __init__(self):
        self.text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=1000,
            chunk_overlap=200,
            length_function=len,
            separators=[
                "\n\n",
                "\n",
                "。|！|？",
                ".",
                "!",
                "?",
                " ",
                ""
            ]
        )

    def load_file(self, file_path: str) -> List[Document]:
        """加载单个文件"""
        path = Path(file_path)
        suffix = path.suffix.lower()

        if suffix == ".pdf":
            return self._load_pdf(file_path)
        elif suffix == ".txt":
            return self._load_txt(file_path)
        elif suffix == ".md":
            return self._load_md(file_path)
        elif suffix in [".doc", ".docx"]:
            return self._load_docx(file_path)
        elif suffix in [".html", ".htm"]:
            return self._load_html(file_path)
        else:
            raise ValueError(f"不支持的文件类型: {suffix}")

    def _load_pdf(self, file_path: str) -> List[Document]:
        """加载 PDF 文件"""
        loader = PyPDFLoader(file_path)
        pages = loader.load_and_split()
        
        # 分割文档
        documents = self.text_splitter.split_documents(pages)
        return documents

    def _load_txt(self, file_path: str) -> List[Document]:
        """加载 TXT 文件"""
        loader = TextLoader(file_path, encoding="utf-8")
        documents = loader.load()
        
        # 分割文档
        return self.text_splitter.split_documents(documents)

    def _load_md(self, file_path: str) -> List[Document]:
        """加载 Markdown 文件"""
        loader = UnstructuredMarkdownLoader(file_path)
        documents = loader.load()
        
        # 分割文档
        return self.text_splitter.split_documents(documents)

    def _load_docx(self, file_path: str) -> List[Document]:
        """加载 Word 文档"""
        loader = UnstructuredWordDocumentLoader(file_path)
        documents = loader.load()
        
        # 分割文档
        return self.text_splitter.split_documents(documents)

    def _load_html(self, file_path: str) -> List[Document]:
        """加载 HTML 文件"""
        loader = BSHTMLLoader(file_path)
        documents = loader.load()
        
        # 分割文档
        return self.text_splitter.split_documents(documents)

    def load_directory(self, directory: str) -> List[Document]:
        """加载目录下的所有文件"""
        all_documents = []
        
        for root, dirs, files in os.walk(directory):
            for file in files:
                if file.startswith("."):
                    continue
                    
                file_path = os.path.join(root, file)
                try:
                    documents = self.load_file(file_path)
                    # 添加来源信息
                    for doc in documents:
                        doc.metadata["source"] = file_path
                        doc.metadata["file_name"] = file
                    all_documents.extend(documents)
                except Exception as e:
                    print(f"加载文件失败 {file_path}: {e}")
        
        return all_documents


class KnowledgeBaseManager:
    """知识库管理器"""

    def __init__(self):
        self.loader = DocumentLoader()
        self.knowledge_path = settings.knowledge_path

    def load_knowledge(self) -> List[Document]:
        """加载知识库"""
        if not os.path.exists(self.knowledge_path):
            os.makedirs(self.knowledge_path, exist_ok=True)
            return []
        
        return self.loader.load_directory(self.knowledge_path)

    def add_document(self, content: str, metadata: dict = None) -> Document:
        """添加单个文档"""
        if metadata is None:
            metadata = {}
        
        doc = Document(
            page_content=content,
            metadata=metadata
        )
        
        # 分割文档
        docs = self.loader.text_splitter.split_documents([doc])
        return docs


# 全局实例
document_loader = DocumentLoader()
knowledge_base_manager = KnowledgeBaseManager()
