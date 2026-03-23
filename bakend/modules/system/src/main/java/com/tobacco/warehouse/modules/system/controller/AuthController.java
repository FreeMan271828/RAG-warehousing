package com.tobacco.warehouse.modules.system.controller;

import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.common.security.utils.JwtUtils;
import com.tobacco.warehouse.modules.system.entity.SysUser;
import com.tobacco.warehouse.modules.system.entity.SysRole;
import com.tobacco.warehouse.modules.system.entity.SysPermission;
import com.tobacco.warehouse.modules.system.mapper.SysUserMapper;
import com.tobacco.warehouse.modules.system.mapper.SysRoleMapper;
import com.tobacco.warehouse.modules.system.mapper.SysUserRoleMapper;
import com.tobacco.warehouse.modules.system.mapper.SysRolePermissionMapper;
import com.tobacco.warehouse.modules.system.mapper.SysPermissionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 认证 Controller
 *
 * @author warehouse
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // 从数据库查询用户
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("用户名或密码错误");
        }


        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), username);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());

        return Result.success("登录成功", data);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        // 从请求头获取Token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未登录或Token无效");
        }
        
        String tokenStr = token.substring(7);
        
        // 解析Token获取用户ID
        Long userId;
        String username;
        try {
            userId = jwtUtils.getUserId(tokenStr);
            username = jwtUtils.getUsername(tokenStr);
        } catch (Exception e) {
            return Result.error("Token解析失败");
        }
        
        // 根据用户ID获取用户信息
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 获取用户角色
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(user.getId());
        List<SysRole> roles = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysRole role = sysRoleMapper.selectById(roleId);
            if (role != null) {
                roles.add(role);
            }
        }

        // 获取角色权限
        Set<String> permissionCodes = new HashSet<>();
        for (Long roleId : roleIds) {
            List<Long> permIds = sysRolePermissionMapper.selectPermissionIdsByRoleId(roleId);
            for (Long permId : permIds) {
                SysPermission perm = sysPermissionMapper.selectById(permId);
                if (perm != null && perm.getStatus() == 1) {
                    permissionCodes.add(perm.getPermissionCode());
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("avatar", user.getAvatar());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("post", user.getPost());
        data.put("deptId", user.getDeptId());
        data.put("status", user.getStatus());
        data.put("isAdmin", user.getIsAdmin());
        data.put("loginIp", user.getLoginIp());
        data.put("loginTime", user.getLoginTime());
        data.put("createTime", user.getCreateTime());
        
        // 角色信息
        List<Map<String, Object>> roleList = new ArrayList<>();
        for (SysRole role : roles) {
            Map<String, Object> roleMap = new HashMap<>();
            roleMap.put("roleId", role.getId());
            roleMap.put("roleCode", role.getRoleCode());
            roleMap.put("roleName", role.getRoleName());
            roleMap.put("roleType", role.getRoleType());
            roleList.add(roleMap);
        }
        data.put("roles", roleList);
        data.put("roleCodes", roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList()));
        data.put("roleNames", roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        
        // 权限信息
        List<Map<String, Object>> permissionList = new ArrayList<>();
        for (Long roleId : roleIds) {
            List<Long> permIds = sysRolePermissionMapper.selectPermissionIdsByRoleId(roleId);
            for (Long permId : permIds) {
                SysPermission perm = sysPermissionMapper.selectById(permId);
                if (perm != null && perm.getStatus() == 1) {
                    Map<String, Object> permMap = new HashMap<>();
                    permMap.put("permissionId", perm.getId());
                    permMap.put("permissionCode", perm.getPermissionCode());
                    permMap.put("permissionName", perm.getPermissionName());
                    permMap.put("permissionType", perm.getPermissionType());
                    permissionList.add(permMap);
                    permissionCodes.add(perm.getPermissionCode());
                }
            }
        }
        // 去重
        List<Map<String, Object>> distinctPermissions = permissionList.stream()
            .collect(Collectors.toMap(
                m -> (String) m.get("permissionCode"), 
                m -> m, 
                (m1, m2) -> m1
            ))
            .values()
            .stream()
            .toList();
        
        data.put("permissions", distinctPermissions);
        data.put("permissionCodes", permissionCodes);
        
        return Result.success(data);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        String realName = request.getRealName();

        // 验证参数
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (!password.equals(confirmPassword)) {
            return Result.error("两次输入的密码不一致");
        }
        if (password.length() < 4) {
            return Result.error("密码长度至少4位");
        }

        // 检查用户名是否已存在
        SysUser existUser = sysUserMapper.selectByUsername(username);
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 创建新用户
        SysUser newUser = new SysUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRealName(realName != null ? realName : username);
        newUser.setStatus(1);
        newUser.setIsAdmin(0);
        
        // 设置创建时间和更新时间
        newUser.setCreateTime(java.time.LocalDateTime.now());
        newUser.setUpdateTime(java.time.LocalDateTime.now());

        // 保存用户
        sysUserMapper.insert(newUser);

        // 生成Token
        String token = jwtUtils.generateToken(newUser.getId(), username);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", newUser.getId());
        data.put("username", newUser.getUsername());

        return Result.success("注册成功", data);
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

    /**
     * 注册请求
     */
    public static class RegisterRequest {
        private String username;
        private String password;
        private String confirmPassword;
        private String realName;

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

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }
    }
}
