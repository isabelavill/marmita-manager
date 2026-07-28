package com.isabelavill.marmitamanager.service;

import com.isabelavill.marmitamanager.dto.PedidoRequestDTO;
import com.isabelavill.marmitamanager.dto.PedidoResponseDTO;
import com.isabelavill.marmitamanager.dto.WebhookPagamentoDTO;
import com.isabelavill.marmitamanager.entity.Cliente;
import com.isabelavill.marmitamanager.entity.Pedido;
import com.isabelavill.marmitamanager.entity.StatusPedido;
import com.isabelavill.marmitamanager.repository.ClienteRepository;
import com.isabelavill.marmitamanager.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import com.isabelavill.marmitamanager.service.S3Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final S3Service s3Service;


    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository, S3Service s3Service) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.s3Service = s3Service;

    }

    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + dto.clienteId()));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDescricao(dto.descricao());
        pedido.setValorTotal(dto.valorTotal());

        Pedido salvo = pedidoRepository.save(pedido);
        return toResponseDTO(salvo);
    }

    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAllComCliente()
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
    return toResponseDTO(pedido, null);
}

    private PedidoResponseDTO toResponseDTO(Pedido pedido, String chaveComprovante) {
        return new PedidoResponseDTO(
            pedido.getId(),
            pedido.getCliente().getNome(),
            pedido.getDescricao(),
            pedido.getValorTotal(),
            pedido.getStatus(),
            pedido.getCriadoEm(),
            chaveComprovante
        );
    }

    public PedidoResponseDTO confirmarPagamento(WebhookPagamentoDTO webhook) {
        Pedido pedido = pedidoRepository.findById(webhook.pedidoId())
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + webhook.pedidoId()));

        if (pedido.getTransacaoId() != null && pedido.getTransacaoId().equals(webhook.transacaoId())) {
            return toResponseDTO(pedido);
        }

        if (!"CONFIRMADO".equalsIgnoreCase(webhook.statusPagamento())) {
            throw new IllegalArgumentException("Status de pagamento não reconhecido: " + webhook.statusPagamento());
        }

        pedido.setTransacaoId(webhook.transacaoId());
        pedido.setStatus(StatusPedido.PAGO);

        Pedido atualizado = pedidoRepository.save(pedido);

        // Gera e salva o comprovante no S3
        String conteudoComprovante = """
            COMPROVANTE DE PAGAMENTO
            -------------------------
            Pedido: %d
            Cliente: %s
            Descrição: %s
            Valor: R$ %s
            Transação: %s
            Status: PAGO
            """.formatted(
                atualizado.getId(),
                atualizado.getCliente().getNome(),
                atualizado.getDescricao(),
                atualizado.getValorTotal(),
                atualizado.getTransacaoId()
            );

        String chaveComprovante = s3Service.salvarComprovante(conteudoComprovante, atualizado.getId());

        return toResponseDTO(atualizado, chaveComprovante);
    }    
}