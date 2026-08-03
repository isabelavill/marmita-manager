package com.isabelavill.marmitamanager.repository;

import com.isabelavill.marmitamanager.entity.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClienteRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("marmita_test")
        .withUsername("test_user")
        .withPassword("test_pass");

    @DynamicPropertySource
    static void configurarPropriedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void deveSalvarEBuscarClientePorEmail() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setNome("Isabela Teste");
        cliente.setEmail("integracao@teste.com");
        cliente.setTelefone("81988887777");

        // Act
        clienteRepository.save(cliente);
        Optional<Cliente> encontrado = clienteRepository.findByEmail("integracao@teste.com");

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Isabela Teste");
    }

    @Test
    void naoDeveEncontrarClienteComEmailInexistente() {
        Optional<Cliente> resultado = clienteRepository.findByEmail("naoexiste@teste.com");

        assertThat(resultado).isEmpty();
    }
}