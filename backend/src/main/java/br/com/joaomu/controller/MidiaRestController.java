package br.com.joaomu.controller;

import br.com.joaomu.service.UploadService;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/midia")
public class MidiaRestController {

    private final UploadService uploadService;
    private final S3Client s3Client;

    // ============================================================
    // Métodos que gerenciam o upload e download de imagens
    // ============================================================

    // Injeção do nome do bucket do MinIO configurado no application.properties;
    // necessário para o método obterImagem saber de qual bucket deve baixar
    // os bytes
    @Value("${minio.bucket-name}")
    private String bucketName;

    public MidiaRestController(UploadService uploadService, S3Client s3Client) {
        this.uploadService = uploadService;
        this.s3Client = s3Client;
    }

    // Mapeamento de rota para o upload
    // Map<String, String> = <chave, valor>, usado pra retornar um JSON
    // @RequestParam = captura parâmetros do request
    // @MultipartFile = arquivo enviado no request
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImagem(@RequestParam("file") MultipartFile file) {
        try {
            // UploadImage(file): Envio do arquivo ao MinIO e retorno do caminho relativo
            String url = uploadService.uploadImage(file);

            // Retorna um JSON com a chave "imageUrl" e o valor do caminho relativo
            // com o prefixo "/api" porque as rotas do backend começam com /api/**
            // na configuração do CORS/ProxyFilter
            return ResponseEntity.ok(Map.of("imageUrl", "/api" + url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao fazer upload da imagem: " + e.getMessage()));
        }
    }

    // Endpoint proxy para recuperar a imagem do MinIO de forma segura
    // Configura uma rota http dinâmica com {filename}, que representa o nome do
    // arquivo
    // salvo via UUID (do uploadImage)
    // @PathVariable retira o valor da URL {filename}
    // ResponseEntity<byte[]> = retorna os bytes da imagem
    // MediaType.parseMediaType = define o tipo de conteúdo (imagem)
    // objectBytes.response().contentType() = obtém o tipo de conteúdo do arquivo

    // GetObjectRequest.builder() = cria um construtor de GetObjectRequest
    // que cria a requisição de busca do arquivo no MinIO usando o bucket
    // ligado a key que é o arquivo gerado via UUID (do uploadImage)
    // .bucket(bucketName) = define o bucket (pasta)
    // .key(fileName) = define a chave (nome do arquivo)
    // .build() = constrói o objeto

    // s3Client.getObjectAsBytes(getObjectRequest) = obtém os bytes do arquivo
    // No final, há a leitura tipo de arquivo com o método "contentType"
    // (image/png, image/jpeg, etc) e a inserção no cabeçalho HTTP Content-Type
    // pelo mesmo.. isso faz com que o navegador saiba que deve renderizar a imagem
    // e não baixar um arquivo binário genérico
    // ResponseBytes<GetObjectResponse> = wrapper que contém os bytes e a response

    // Preservar a extensão com {fileName:.+}
    // {fileName:.+} evita que o ponto seja removido na URL
    @GetMapping("/imagens/{fileName:.+}")
    public ResponseEntity<byte[]> obterImagem(@PathVariable String fileName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

            String rawContentType = objectBytes.response().contentType();
            MediaType mediaType = resolveMediaType(fileName, rawContentType);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(objectBytes.asByteArray());
        } catch (Exception e) {
            System.err.println("Erro ao buscar imagem '" + fileName + "' no MinIO: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    private MediaType resolveMediaType(String fileName, String rawContentType) {
        if (rawContentType != null && !rawContentType.isBlank()
                && !rawContentType.equalsIgnoreCase("application/octet-stream")) {
            try {
                return MediaType.parseMediaType(rawContentType);
            } catch (Exception ignored) {
            }
        }
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png"))
            return MediaType.IMAGE_PNG;
        if (lower.endsWith(".gif"))
            return MediaType.IMAGE_GIF;
        if (lower.endsWith(".webp"))
            return MediaType.parseMediaType("image/webp");
        if (lower.endsWith(".svg"))
            return MediaType.parseMediaType("image/svg+xml");
        return MediaType.IMAGE_JPEG;
    }
}