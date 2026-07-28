package com.isabelavill.marmitamanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String salvarComprovante(String conteudo, Long pedidoId) {
        String chave = "comprovantes/pedido-%d-%s.txt".formatted(pedidoId, UUID.randomUUID());

        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(chave)
            .build();

        s3Client.putObject(request, RequestBody.fromString(conteudo));

        return chave;
    }
}