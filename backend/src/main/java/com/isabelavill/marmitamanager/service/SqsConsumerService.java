package com.isabelavill.marmitamanager.service;
import com.isabelavill.marmitamanager.entity.Pedido;
import com.isabelavill.marmitamanager.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SqsConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(SqsConsumerService.class);

    private final SqsClient sqsClient;
    private final PedidoRepository pedidoRepository;
    private final S3Service s3Service;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public SqsConsumerService(SqsClient sqsClient, PedidoRepository pedidoRepository, S3Service s3Service) {
        this.sqsClient = sqsClient;
        this.pedidoRepository = pedidoRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void processarMensagens() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(5)
            .waitTimeSeconds(2)
            .build();

        List<Message> mensagens = sqsClient.receiveMessage(receiveRequest).messages();

        for (Message mensagem : mensagens) {
            try {
                Long pedidoId = Long.parseLong(mensagem.body());
                gerarComprovante(pedidoId);

                // Remove a mensagem da fila só depois de processar com sucesso
                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(mensagem.receiptHandle())
                    .build());

                logger.info("Comprovante gerado com sucesso para pedido {}", pedidoId);
            } catch (Exception e) {
                logger.error("Erro ao processar mensagem da fila: {}", mensagem.body(), e);
                // Não remove a mensagem - ela volta a ficar visível na fila após o visibility timeout,
                // permitindo nova tentativa de processamento
            }
        }
    }

    @Transactional
    private void gerarComprovante(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

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
                pedido.getId(),
                pedido.getCliente().getNome(),
                pedido.getDescricao(),
                pedido.getValorTotal(),
                pedido.getTransacaoId()
            );

        s3Service.salvarComprovante(conteudoComprovante, pedido.getId());
    }
}