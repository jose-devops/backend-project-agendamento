
package com.api.app.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String senha;
}
