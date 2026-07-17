package com.isabelavill.marmitamanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WebhookPagamentoDTO(

    @NotNull(message = "ID do pedido é obrigatório")
    Long pedidoId,

    @NotBlank(message = "ID da transação é obrigatório")
    String transacaoId,

    @NotBlank(message = "Status é obrigatório")
    String statusPagamento
) {}