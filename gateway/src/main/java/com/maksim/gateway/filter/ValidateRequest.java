package com.maksim.gateway.filter;

//import lombok.Data;

//@Data
public class ValidateRequest {
    private String token;
    public ValidateRequest(String token) { this.token = token; }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

