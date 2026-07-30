package br.com.joaomu.service;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

/* 
    Classe responsável pelo envio de arquivos de imagem ao
    MinIO, que é um serviço de armazenamento de objetos
    compatível com o AWS S3 (configurado no S3Config.java)
*/

// Registro no container de inversão de controle IoC 
// do Spring; injeção automática em outras classes
// (controllers)
@Service
public class UploadService {

    // Cliente responsável por interagir com o S3
    // final para garantir a imutabilidade após injeção
    private final S3Client s3Client;
    // Nome do bucket onde as imagens serão salvas
    private final String bucketName;

    // Construtor, responsável por receber a configuração do S3 e o nome do bucket
    public UploadService(
            S3Client s3Client,
            @Value("${minio.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    // @PostConstruct é uma anotação usada para sinalizar que o
    // método deve ser executado uma vez após a inicialização
    // da classe Spring; assim que o Spring termina de iniciar
    // a aplicação, ele executa o método init() e injeta as
    // dependências
    @PostConstruct
    public void init() {
        // Cria o bucket ao iniciar a aplicação caso ele não exista
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }
    }

    public String uploadImage(MultipartFile file) {
        // Gera um nome único para o arquivo para evitar colisões
        // extension puxa a extensão;
        // fileName usa do UUID que gera um id único de 36 caracteres
        // se dois usuários fizerem o upload de "foto.png" não há colisão
        String extension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
        try {
            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank() || contentType.equalsIgnoreCase("application/octet-stream")) {
                contentType = resolveContentTypeFromExtension(extension);
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            // Retorna a rota proxy da API para obter a imagem,
            // isso serve para ocultar o endereço real do storage por segurança;
            // e controlar o acesso às imagens por um Controller do Spring
            return "/midia/imagens/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar a imagem", e);
        }
    }

    // Método utilitário usado no uploadImage, só recupera
    // o tipo (extensão) do arquivo
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String resolveContentTypeFromExtension(String extension) {
        if (extension == null) return "image/jpeg";
        switch (extension.toLowerCase()) {
            case "png":
                return "image/png";
            case "webp":
                return "image/webp";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "jfif":
            case "jpg":
            case "jpeg":
            default:
                return "image/jpeg";
        }
    }
}