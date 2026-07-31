package br.com.joaomu.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Exchange + 2 filas (export e email) + binding

// Ciclo de vida:
// Produtor -> Exchange -> (Routing Key) -> Fila -> Consumidor
@Configuration
public class RabbitMQConfig {

    // ================================================================
    // Exchange única = todas as filas do BatchOut usam a mesma exchange
    // TopicExchange permite roteamento baseado em padrão (routing keys)
    // ================================================================
    public static final String EXCHANGE_BATCHOUT = "batchout.exchange";

    // ================================================================
    // Fila 1: Exportação Batch (o coração do projeto)
    // Produtor: ExportJobService | Consumidor: ExportJobListener
    // ================================================================
    public static final String QUEUE_EXPORT = "batchout.export.queue";
    public static final String ROUTING_KEY_EXPORT = "batchout.export.routingkey";

    // ================================================================
    // Fila 2: Notificações por e-mail (exportação concluída)
    // Produtor: ExportJobListener | Consumidor: NotificacaoListener
    // ================================================================
    public static final String QUEUE_EMAIL = "batchout.email.queue";
    public static final String ROUTING_KEY_EMAIL = "batchout.email.routingkey";

    // ================================================================
    // Beans de infraestrutura
    // ================================================================

    // Exchange principal, única no sistema
    // Não armazenada nada.. só recebe a mensagem e decide pra qual fila enviar
    // usando a Routing Key
    @Bean
    public TopicExchange batchoutExchange() {
        return new TopicExchange(EXCHANGE_BATCHOUT);
    }

    // Fila de exportação - durable=true: sobrevive a restart do RabbitMQ
    // As filas armazenam as mensagens até que o consumer, worker esteja
    // disponível para processar

    // durable = true, para as duas filas, garante que as mensagens não sejam
    // perdidas se o RabbitMQ for reiniciado
    @Bean
    public Queue exportQueue() {
        return new Queue(QUEUE_EXPORT, true);
    }

    // Fila de e-mail - durable=true
    @Bean
    public Queue emailQueue() {
        return new Queue(QUEUE_EMAIL, true);
    }

    // Esse binding faz com que a fila "exportQueue" receba mensagens com a
    // chave "batchout.export.routingkey".. é responsável pela conexão

    // Todas as mensagens publicas no batchoutExchange com a routing key
    // "batchout.export.routingkey" serão enviadas para a fila exportQueue
    @Bean
    public Binding bindingExport(Queue exportQueue, TopicExchange batchoutExchange) {
        return BindingBuilder.bind(exportQueue)
                .to(batchoutExchange)
                .with(ROUTING_KEY_EXPORT);
    }

    // Bean de conversão (transforma POJOs em JSON e vice-versa)
    // Por padrão, o Spring AMQP usa a serialização padrão do Java (binário Java),
    // e isso quebra interoperabilidade caso Nodejs ou Python fosse usado,
    // além de ser propenso a erro de versão de classe como Serializable
    // Esse Bean altera o comportamento global do Spring AMQP
    // para converter qualquer objeto Java em JSON na hora de enviar
    // e converter JSON em objeto Java na hora de receber
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
