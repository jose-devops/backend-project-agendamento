package com.api.app.controllers;

import com.api.app.dtos.ProprietarioDTO;
import com.api.app.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registrar-proprietario")
    public ResponseEntity<?> registrarProprietario(@RequestBody ProprietarioDTO dto) {
        authService.registrarProprietario(dto);
        return ResponseEntity.ok("Proprietário cadastrado com sucesso!");
    }
}
