package com.isabelavill.marmitamanager.controller;

import com.isabelavill.marmitamanager.dto.LoginRequestDTO;
import com.isabelavill.marmitamanager.dto.RegistroRequestDTO;
import com.isabelavill.marmitamanager.dto.TokenResponseDTO;
import com.isabelavill.marmitamanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<TokenResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}