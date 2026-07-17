package com.isabelavill.marmitamanager.service;

import com.isabelavill.marmitamanager.dto.PedidoRequestDTO;
import com.isabelavill.marmitamanager.dto.PedidoResponseDTO;
import com.isabelavill.marmitamanager.entity.Cliente;
import com.isabelavill.marmitamanager.entity.Pedido;
import com.isabelavill.marmitamanager.repository.ClienteRepository;
import com.isabelavill.marmitamanager.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
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
        return new PedidoResponseDTO(
            pedido.getId(),
            pedido.getCliente().getNome(), // <-- aqui é onde o N+1 vai acontecer
            pedido.getDescricao(),
            pedido.getValorTotal(),
            pedido.getStatus(),
            pedido.getCriadoEm()
        );
    }
}