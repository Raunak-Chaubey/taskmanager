package com.raunak.taskmanager.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private String tokenType;
    private long expiresInMs;
    private UserSummary user;

    @Data
    @Builder
    public static class UserSummary {
        private Long id;
        private String username;
        private String email;
        private String role;
    }
}
