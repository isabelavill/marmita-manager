package com.isabelavill.marmitamanager.service;

import com.isabelavill.marmitamanager.dto.ClienteRequestDTO;
import com.isabelavill.marmitamanager.dto.ClienteResponseDTO;
import com.isabelavill.marmitamanager.entity.Cliente;
import com.isabelavill.marmitamanager.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deveCriarClienteComSucesso() {
        // Arrange
        ClienteRequestDTO dto = new ClienteRequestDTO("Isabela", "isabela@teste.com", "81999999999");

        when(clienteRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente clienteSalvo = invocation.getArgument(0);
            clienteSalvo.setId(1L);
            return clienteSalvo;
        });

        // Act
        ClienteResponseDTO resultado = clienteService.criar(dto);

        // Assert
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Isabela");
        assertThat(resultado.email()).isEqualTo("isabela@teste.com");

        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Arrange
        ClienteRequestDTO dto = new ClienteRequestDTO("Isabela", "isabela@teste.com", "81999999999");
        Cliente clienteExistente = new Cliente();
        clienteExistente.setEmail("isabela@teste.com");

        when(clienteRepository.findByEmail(dto.email())).thenReturn(Optional.of(clienteExistente));

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> clienteService.criar(dto));

        // Confirma que NUNCA tentou salvar, já que a validação deveria barrar antes
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontradoPorId() {
        // Arrange
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> clienteService.buscarPorId(999L));
    }
}