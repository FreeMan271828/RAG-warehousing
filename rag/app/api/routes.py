"""
RAG API 路由
"""
import os
from typing import List, Optional
from fastapi import APIRouter, HTTPException, Query, Body
from pydantic import BaseModel, Field
from app.chains.rag_chain import rag_chain, conversation_manager
from app.vectorstores.chroma_store import vectorstore_manager
from app.document_loaders.loader import knowledge_base_manager
from app.core.config import get_settings

settings = get_settings()
router = APIRouter(prefix="/api/v1/rag", tags=["RAG"])


# ==================== 请求/响应模型 ====================

class ChatRequest(BaseModel):
    """问答请求"""
    question: str = Field(..., description="用户问题")
    conversation_id: Optional[str] = Field(None, description="对话ID")
    history: Optional[List[dict]] = Field(default_factory=list, description="对话历史")


class ChatResponse(BaseModel):
    """问答响应"""
    answer: str = Field(..., description="回答内容")
    sources: List[dict] = Field(default_factory=list, description="来源文档")
    conversation_id: str = Field(..., description="对话ID")


class DocumentRequest(BaseModel):
    """添加文档请求"""
    content: str = Field(..., description="文档内容")
    title: Optional[str] = Field(None, description="文档标题")
    doc_type: Optional[str] = Field(None, description="文档类型")


class SearchRequest(BaseModel):
    """搜索请求"""
    keyword: str = Field(..., description="搜索关键词")
    k: int = Field(4, description="返回结果数量")


# ==================== API 接口 ====================

@router.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    """
    智能问答接口
    
    用户发送问题，返回基于知识库的智能回答
    """
    if not request.question or not request.question.strip():
        raise HTTPException(status_code=400, detail="问题不能为空")
    
    # 生成或使用对话ID
    conversation_id = request.conversation_id or "default"
    
    # 调用 RAG 链回答问题
    result = rag_chain.answer(
        question=request.question,
        conversation_history=request.history
    )
    
    # 保存对话历史
    conversation_manager.add_message(
        conversation_id=conversation_id,
        question=request.question,
        answer=result["answer"]
    )
    
    return ChatResponse(
        answer=result["answer"],
        sources=result.get("sources", []),
        conversation_id=conversation_id
    )


@router.get("/history/{conversation_id}")
async def get_history(
    conversation_id: str,
    limit: int = Query(10, ge=1, le=100, description="返回记录数")
):
    """
    获取对话历史
    """
    history = conversation_manager.get_history(conversation_id, limit)
    return {
        "conversation_id": conversation_id,
        "history": history,
        "total": len(history)
    }


@router.get("/suggestions")
async def get_suggestions():
    """
    获取推荐问题
    """
    suggestions = [
        "如何进行设备保养？",
        "电池充电时需要注意什么？",
        "设备出现故障如何报修？",
        "零件库存不足怎么办？",
        "如何查看设备运行状态？",
        "保养计划如何制定？",
        "电池故障如何处理？",
        "如何添加新的设备？"
    ]
    return {
        "suggestions": suggestions
    }


@router.post("/document")
async def add_document(request: DocumentRequest):
    """
    添加文档到知识库
    """
    try:
        # 创建文档
        metadata = {
            "doc_type": request.doc_type or "general",
            "title": request.title or ""
        }
        
        docs = knowledge_base_manager.add_document(
            content=request.content,
            metadata=metadata
        )
        
        # 添加到向量存储
        vectorstore_manager.add_documents(docs)
        
        return {
            "success": True,
            "message": f"成功添加 {len(docs)} 个文档块",
            "chunks": len(docs)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"添加文档失败: {str(e)}")


@router.get("/search")
async def search(
    keyword: str = Query(..., description="搜索关键词"),
    k: int = Query(4, ge=1, le=20, description="返回结果数")
):
    """
    搜索知识库
    """
    if not keyword or not keyword.strip():
        raise HTTPException(status_code=400, detail="关键词不能为空")
    
    results = rag_chain.similarity_search_only(keyword, k=k)
    
    return {
        "keyword": keyword,
        "results": results,
        "total": len(results)
    }


@router.post("/knowledge/init")
async def init_knowledge():
    """
    初始化知识库
    从指定目录加载文档并构建向量索引
    """
    try:
        # 检查知识库目录
        knowledge_path = settings.knowledge_path
        if not os.path.exists(knowledge_path):
            os.makedirs(knowledge_path, exist_ok=True)
            return {
                "success": True,
                "message": f"知识库目录已创建: {knowledge_path}，请添加文档后重新初始化",
                "documents": 0
            }
        
        # 加载文档
        documents = knowledge_base_manager.load_knowledge()
        
        if not documents:
            return {
                "success": True,
                "message": f"知识库目录为空: {knowledge_path}",
                "documents": 0
            }
        
        # 创建向量存储
        vectorstore_manager.create_vectorstore(documents)
        
        return {
            "success": True,
            "message": f"成功加载 {len(documents)} 个文档块",
            "documents": len(documents)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"初始化知识库失败: {str(e)}")


@router.delete("/knowledge/clear")
async def clear_knowledge():
    """
    清空知识库
    """
    try:
        vectorstore_manager.delete_collection()
        
        return {
            "success": True,
            "message": "知识库已清空"
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"清空知识库失败: {str(e)}")


@router.get("/knowledge/stats")
async def get_knowledge_stats():
    """
    获取知识库统计信息
    """
    count = vectorstore_manager.get_document_count()
    
    return {
        "document_count": count,
        "knowledge_path": settings.knowledge_path,
        "chroma_path": settings.chroma_persist_dir
    }
