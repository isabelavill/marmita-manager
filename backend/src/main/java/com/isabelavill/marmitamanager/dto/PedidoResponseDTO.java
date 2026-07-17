package com.isabelavill.marmitamanager.dto;

import com.isabelavill.marmitamanager.entity.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResponseDTO(
    Long id,
    String clienteNome,
    String descricao,
    BigDecimal valorTotal,
    StatusPedido status,
    LocalDateTime criadoEm
) {}