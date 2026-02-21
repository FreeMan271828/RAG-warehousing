package com.tobacco.warehouse.modules.rag.controller;

import com.tobacco.warehouse.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * RAG 智能问答 Controller
 * 基于知识库的智能问答服务
 *
 * @author warehouse
 */
@Slf4j
@Tag(name = "智能问答")
@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
public class RagController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${rag.api.url:http://localhost:8000}")
    private String ragApiUrl;

    @Value("${rag.api.key:}")
    private String ragApiKey;

    @Operation(summary = "智能问答")
    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        if (question == null || question.trim().isEmpty()) {
            return Result.error("问题不能为空");
        }

        try {
            // 调用RAG服务API
            String url = ragApiUrl + "/chat";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (ragApiKey != null && !ragApiKey.isEmpty()) {
                headers.set("Authorization", "Bearer " + ragApiKey);
            }

            Map<String, Object> body = new HashMap<>();
            body.put("question", question);
            body.put("history", request.getOrDefault("history", String.valueOf(new ArrayList<>())));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("answer", response.getBody().get("answer"));
                result.put("sources", response.getBody().getOrDefault("sources", new ArrayList<>()));
                result.put("conversationId", UUID.randomUUID().toString());
                return Result.success(result);
            }

            return Result.error("调用智能问答服务失败");
        } catch (Exception e) {
            log.error("调用RAG服务失败: {}", e.getMessage(), e);
            return Result.error("智能问答服务暂时不可用: " + e.getMessage());
        }
    }

    @Operation(summary = "获取对话历史")
    @GetMapping("/history/{conversationId}")
    public Result<List<Map<String, String>>> getHistory(@PathVariable String conversationId) {
        // TODO: 从缓存或数据库获取对话历史
        return Result.success(new ArrayList<>());
    }

    @Operation(summary = "获取推荐问题")
    @GetMapping("/suggestions")
    public Result<List<String>> getSuggestions() {
        // 返回一些预设的推荐问题
        List<String> suggestions = Arrays.asList(
                "如何进行设备保养？",
                "电池充电时需要注意什么？",
                "设备出现故障如何报修？",
                "零件库存不足怎么办？",
                "如何查看设备运行状态？"
        );
        return Result.success(suggestions);
    }

    @Operation(summary = "上传文档到知识库")
    @PostMapping("/document")
    public Result<Boolean> uploadDocument(@RequestBody Map<String, String> request) {
        String docType = request.get("docType");
        String content = request.get("content");
        
        if (content == null || content.trim().isEmpty()) {
            return Result.error("文档内容不能为空");
        }

        try {
            // 调用RAG服务API添加文档
            String url = ragApiUrl + "/document";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (ragApiKey != null && !ragApiKey.isEmpty()) {
                headers.set("Authorization", "Bearer " + ragApiKey);
            }

            Map<String, Object> body = new HashMap<>();
            body.put("content", content);
            body.put("docType", docType);
            body.put("title", request.getOrDefault("title", ""));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            return Result.success(response.getStatusCode() == HttpStatus.OK);
        } catch (Exception e) {
            log.error("上传文档到知识库失败: {}", e.getMessage(), e);
            return Result.error("上传文档失败: " + e.getMessage());
        }
    }

    @Operation(summary = "搜索知识库")
    @GetMapping("/search")
    public Result<List<Map<String, Object>>> search(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }

        try {
            String url = ragApiUrl + "/search?keyword=" + keyword;
            
            HttpHeaders headers = new HttpHeaders();
            if (ragApiKey != null && !ragApiKey.isEmpty()) {
                headers.set("Authorization", "Bearer " + ragApiKey);
            }

            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Result.success(response.getBody());
            }

            return Result.success(new ArrayList<>());
        } catch (Exception e) {
            log.error("搜索知识库失败: {}", e.getMessage(), e);
            return Result.error("搜索失败: " + e.getMessage());
        }
    }
}
