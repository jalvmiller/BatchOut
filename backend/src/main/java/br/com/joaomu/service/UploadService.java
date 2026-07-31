package br.com.joaomu.service;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
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
    private final String bucketName;
    // S3Presigner é responsável por gerar as Pre-signed URLs temporárias
    private final S3Presigner s3Presigner;

    public UploadService(
            S3Client s3Client,
            S3Presigner s3Presigner,
            @Value("${minio.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
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

    // Upload de bytes brutos (usado pelo Worker para enviar o CSV gerado)
    // Recebe um InputStream + nome da chave no bucket + content type
    public void uploadBytes(InputStream inputStream, String key, String contentType) {
        try {
            byte[] bytes = inputStream.readAllBytes();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar arquivo no MinIO: " + key, e);
        }
    }

    // Gera uma Pre-signed URL para download direto do MinIO.
    // A URL expira após o número de minutos especificado.
    // O frontend usa essa URL para baixar o arquivo sem precisar passar pelo Spring Boot.
    public String gerarPreSignedUrl(String key, int duracaoMinutos) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(duracaoMinutos))
                .getObjectRequest(req -> req.bucket(bucketName).key(key))
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        URL url = presignedRequest.url();
        return url.toString();
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