package com.maksim.auth_service.dto;


import lombok.Data;

@Data
public class TokenRequest {
    private String email;
    private String password;
}
