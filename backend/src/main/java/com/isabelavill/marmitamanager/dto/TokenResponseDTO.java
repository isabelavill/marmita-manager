package com.isabelavill.marmitamanager.dto;

public record TokenResponseDTO(String token, String tipo) {
    public TokenResponseDTO(String token) {
        this(token, "Bearer");
    }
}