"""
RAG Chain 模块
实现基于 LangChain 的问答链
"""
from typing import List, Optional, Dict, Any, Tuple
from langchain.chains import RetrievalQA
from langchain.prompts import PromptTemplate
from langchain_openai import ChatOpenAI
from langchain.schema import Document
from app.vectorstores.chroma_store import vectorstore_manager
from app.core.config import get_settings

settings = get_settings()


# 系统提示词模板
SYSTEM_PROMPT = """你是一个专业的烟厂仓库智能助手，专门帮助用户解答关于设备管理、保养、维修、电池管理等方面的问题。

请根据以下知识库中的内容来回答用户的问题。如果知识库中没有相关信息，请说明"根据我所掌握的知识库信息，无法回答这个问题"，并建议用户查看相关手册或联系专业人员。

回答要求：
1. 准确、简洁、专业
2. 如果需要，可以分步骤说明
3. 如果涉及到操作流程，请尽量详细
4. 如果不确定，请明确说明

知识库内容：
{context}

用户问题：{question}

请给出回答："""


class RAGChain:
    """RAG 问答链"""

    def __init__(self):
        self.llm = self._create_llm()
        self.prompt_template = PromptTemplate(
            template=SYSTEM_PROMPT,
            input_variables=["context", "question"]
        )
        self.qa_chain = None
        self._init_chain()

    def _create_llm(self) -> ChatOpenAI:
        """创建 LLM 实例"""
        return ChatOpenAI(
            model=settings.openai_model,
            openai_api_key=settings.openai_api_key,
            openai_api_base=settings.openai_base_url,
            temperature=0.7,
            max_tokens=2000,
            streaming=False
        )

    def _init_chain(self):
        """初始化问答链"""
        retriever = vectorstore_manager.as_retriever(
            search_kwargs={"k": 4}
        )
        
        if retriever:
            self.qa_chain = RetrievalQA.from_chain_type(
                llm=self.llm,
                chain_type="stuff",
                retriever=retriever,
                return_source_documents=True,
                chain_type_kwargs={
                    "prompt": self.prompt_template
                }
            )

    def answer(
        self,
        question: str,
        conversation_history: Optional[List[Dict]] = None
    ) -> Dict[str, Any]:
        """
        回答问题
        
        Args:
            question: 用户问题
            conversation_history: 对话历史
            
        Returns:
            包含答案和来源的字典
        """
        if self.qa_chain is None:
            return {
                "answer": "知识库尚未初始化，请先初始化知识库。",
                "sources": [],
                "question": question
            }

        try:
            # 如果有对话历史，可以将其加入上下文
            # 这里简化处理，直接使用问题
            result = self.qa_chain({"query": question})
            
            # 提取答案和来源
            answer = result.get("result", "")
            source_documents = result.get("source_documents", [])
            
            # 处理来源文档
            sources = []
            seen_sources = set()
            for doc in source_documents:
                source = doc.metadata.get("source", "未知来源")
                if source not in seen_sources:
                    seen_sources.add(source)
                    sources.append({
                        "content": doc.page_content[:200] + "..." if len(doc.page_content) > 200 else doc.page_content,
                        "source": source,
                        "file_name": doc.metadata.get("file_name", "")
                    })
            
            return {
                "answer": answer,
                "sources": sources,
                "question": question
            }
            
        except Exception as e:
            return {
                "answer": f"处理问题时发生错误：{str(e)}",
                "sources": [],
                "question": question,
                "error": str(e)
            }

    def similarity_search_only(
        self,
        question: str,
        k: int = 4
    ) -> List[Dict[str, Any]]:
        """
        仅进行相似度搜索，不生成回答
        
        Args:
            question: 查询问题
            k: 返回结果数量
            
        Returns:
            相似文档列表
        """
        docs = vectorstore_manager.similarity_search(question, k=k)
        
        results = []
        for doc in docs:
            results.append({
                "content": doc.page_content,
                "source": doc.metadata.get("source", ""),
                "file_name": doc.metadata.get("file_name", "")
            })
        
        return results


class ConversationManager:
    """对话管理器"""

    def __init__(self):
        # 使用内存存储对话历史
        # 生产环境可以使用 Redis 或数据库
        self.conversations: Dict[str, List[Dict]] = {}

    def add_message(
        self,
        conversation_id: str,
        question: str,
        answer: str
    ) -> None:
        """添加对话记录"""
        if conversation_id not in self.conversations:
            self.conversations[conversation_id] = []

        self.conversations[conversation_id].append({
            "question": question,
            "answer": answer
        })

    def get_history(
        self,
        conversation_id: str,
        limit: int = 10
    ) -> List[Dict]:
        """获取对话历史"""
        history = self.conversations.get(conversation_id, [])
        return history[-limit:]

    def clear_history(self, conversation_id: str) -> None:
        """清除对话历史"""
        if conversation_id in self.conversations:
            del self.conversations[conversation_id]


# 全局实例
rag_chain = RAGChain()
conversation_manager = ConversationManager()
