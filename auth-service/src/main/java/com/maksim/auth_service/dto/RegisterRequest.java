package com.maksim.auth_service.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String handle;
    private String password;
}
