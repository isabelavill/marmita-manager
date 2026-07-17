package com.isabelavill.marmitamanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PedidoRequestDTO(

    @NotNull(message = "Cliente é obrigatório")
    Long clienteId,

    @NotBlank(message = "Descrição é obrigatória")
    String descricao,

    @NotNull(message = "Valor total é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    BigDecimal valorTotal
) {}