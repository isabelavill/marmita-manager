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

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final SqsService sqsService;

    public PedidoService(
        PedidoRepository pedidoRepository,
        ClienteRepository clienteRepository,
        SqsService sqsService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.sqsService = sqsService;
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

        // Ao invés de gerar o comprovante aqui (síncrono), manda pra fila
        sqsService.enviarMensagemGerarComprovante(atualizado.getId());

        return toResponseDTO(atualizado);
    }
}