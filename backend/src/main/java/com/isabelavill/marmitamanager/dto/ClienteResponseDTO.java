package com.isabelavill.marmitamanager.dto;

import java.time.LocalDateTime;

public record ClienteResponseDTO(
    Long id,
    String nome,
    String email,
    String telefone,
    LocalDateTime criadoEm
) {}