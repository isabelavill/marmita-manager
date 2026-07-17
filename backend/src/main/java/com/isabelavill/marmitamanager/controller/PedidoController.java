package com.isabelavill.marmitamanager.controller;

import com.isabelavill.marmitamanager.dto.PedidoRequestDTO;
import com.isabelavill.marmitamanager.dto.PedidoResponseDTO;
import com.isabelavill.marmitamanager.dto.WebhookPagamentoDTO;
import com.isabelavill.marmitamanager.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criar(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO criado = pedidoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PostMapping("/webhook/pagamento")
    public ResponseEntity<PedidoResponseDTO> confirmarPagamento(@Valid @RequestBody WebhookPagamentoDTO webhook) {
        PedidoResponseDTO atualizado = pedidoService.confirmarPagamento(webhook);
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }
}