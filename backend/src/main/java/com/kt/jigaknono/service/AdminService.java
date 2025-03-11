package com.kt.jigaknono.service;

import com.kt.jigaknono.domain.Admin;
import com.kt.jigaknono.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;

    // 비밀번호 해시 생성
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 해시 생성 실패", e);
        }
    }

    // 관리자 계정 생성
    public Admin createAdmin(String username, String rawPassword) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(hashPassword(rawPassword));
        return adminRepository.save(admin);
    }

    // 로그인 검증
    public boolean validateAdmin(String username, String rawPassword) {
        return adminRepository.findByUsername(username)
                .map(admin -> admin.getPassword().equals(hashPassword(rawPassword)))
                .orElse(false);
    }

    // 비밀번호 변경
    public boolean changePassword(String currentPassword, String newPassword) {
        Admin admin = adminRepository.findByUsername("admin")
            .orElseThrow(() -> new RuntimeException("관리자 계정을 찾을 수 없습니다."));
        
        if (admin.getPassword().equals(hashPassword(currentPassword))) {
            admin.setPassword(hashPassword(newPassword));
            adminRepository.save(admin);
            return true;
        }
        
        return false;
    }
} 