package com.maksim.auth_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateResponse {
    Integer id;
    String handle;
}
