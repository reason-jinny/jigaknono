package com.kt.jigaknono.controller;

import com.kt.jigaknono.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" })
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 초기 관리자 계정 생성 (처음 한 번만 사용)
    @PostMapping("/setup")
    public ResponseEntity<?> setupAdmin() {
        try {
            // 기본 관리자 계정 생성 (username: admin, password: jigaknono123)
            adminService.createAdmin("admin", "jigaknono123");
            return ResponseEntity.ok().body(Map.of("status", "success", 
                "message", "관리자 계정이 생성되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", 
                "message", e.getMessage()));
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "아이디와 비밀번호를 입력해주세요."
            ));
        }

        if (adminService.validateAdmin(username, password)) {
            return ResponseEntity.ok().body(Map.of("status", "success"));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "아이디 또는 비밀번호가 올바르지 않습니다."
            ));
        }
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        try {
            boolean success = adminService.changePassword(
                passwordData.get("currentPassword"), 
                passwordData.get("newPassword")
            );
            
            if (success) {
                return ResponseEntity.ok().body(Map.of("status", "success"));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "현재 비밀번호가 일치하지 않습니다."
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
} 