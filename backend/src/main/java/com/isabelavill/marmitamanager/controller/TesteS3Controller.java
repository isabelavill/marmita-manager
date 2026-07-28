package com.isabelavill.marmitamanager.controller;

import com.isabelavill.marmitamanager.service.S3Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesteS3Controller {

    private final S3Service s3Service;

    public TesteS3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/teste-s3")
    public String testar() {
        String chave = s3Service.salvarComprovante("Teste de comprovante - pedido 1 - pago", 1L);
        return "Arquivo salvo com a chave: " + chave;
    }
}