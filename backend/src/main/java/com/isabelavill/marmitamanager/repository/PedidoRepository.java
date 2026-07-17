package com.isabelavill.marmitamanager.repository;

import com.isabelavill.marmitamanager.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);
    
    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente")
    List<Pedido> findAllComCliente();
}