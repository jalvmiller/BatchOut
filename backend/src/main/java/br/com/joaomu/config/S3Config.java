package br.com.joaomu.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
// Classes para configurar o cliente S3

/* 
    Essa é a classe de config do Spring Boot para disponibilizar um cliente do AWS S3
    usado pelo serviço de upload; essa classe é anotada com @Configuration
    para que o Spring Boot a reconheça como uma classe de configuração
    e injete o cliente S3 em outras classes que precisam dele

    Aponta para o server local do MinIO; a aplicação consegue fazer download
    e upload de imagens.

    Lembrar:
    1. @Bean
    Anotação que indica que o método retorna um objeto que será gerenciado
    pelo Spring e disponibilizado para injeção de dependência em outras classes.
    2. @Value
    Anotação que permite injetar valores de propriedades do arquivo application.properties
    Anotações de injeção de dependência do Spring Boot que servem para injetar valores
    de propriedades do arquivo application.properties nas variáveis da classe S3Config
    3. Por que forcePathStyle(true)?
    Por padrão, o cliente AWS SDK tenta usar o formato de URL do S3
    http://nome-do-bucket.s3.amazonaws.com
    MinIO funciona melhor no formato
    http://minio:9000/nome-do-bucket
    forcePathStyle(true) força o uso desse segundo formato, garantindo a compatibilidade.
    4. Por que Region.US_EAST_1?
    MinIO ignora a região, mas o SDK do S3 exige
    5. Por que StaticCredentialsProvider?
    O MinIO usa autenticação de usuário e senha (credentials),
    e o StaticCredentialsProvider permite fornecer essas credenciais
    estaticamente para o cliente S3. Isso é comum quando se usa
    servidores locais ou simuladores de S3 que não exigem certificados TLS
    (como o MinIO rodando localmente).Para produção, é recomendável usar
    AWSChainSelfImplementingClient.Builder ou DefaultCredentialsProvider
    para carregar as credenciais de forma mais segura (variáveis de ambiente,
    perfil do AWS CLI, etc.)
*/

@Configuration
public class S3Config {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.US_EAST_1) // MinIO ignora a região, mas o SDK do S3 exige
                .forcePathStyle(true) // Necessário para MinIO e simuladores locais
                .build();
    }
}
