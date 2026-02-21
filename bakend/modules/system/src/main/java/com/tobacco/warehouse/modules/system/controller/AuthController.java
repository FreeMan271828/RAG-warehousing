package com.tobacco.warehouse.modules.system.controller;

import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.common.security.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证 Controller
 *
 * @author warehouse
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        // TODO: 实际实现需要从数据库验证用户
        // 这里简化处理，实际项目中需要查询数据库
        String username = request.getUsername();
        String password = request.getPassword();

        // 模拟登录验证 - 实际项目需要查询数据库
        if ("admin".equals(username) && "admin123".equals(password)) {
            // 生成Token
            Long userId = 1L;
            String token = jwtUtils.generateToken(userId, username);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", userId);
            data.put("username", username);

            return Result.success("登录成功", data);
        }

        return Result.error("用户名或密码错误");
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo() {
        // TODO: 实际实现需要从SecurityContext获取用户信息
        Map<String, Object> data = new HashMap<>();
        data.put("username", "admin");
        data.put("realName", "管理员");
        data.put("avatar", "");
        data.put("roles", new String[]{"admin"});
        data.put("permissions", new String[]{"*"});
        
        return Result.success(data);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // TODO: 可以在这里处理登出逻辑，如将token加入黑名单
        return Result.success();
    }

    /**
     * 登录请求
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
