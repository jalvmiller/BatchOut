package br.com.joaomu.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_EMAIL = "probend.resolucao.email.queue";
    public static final String EXCHANGE_PROBEND = "probend.exchange";
    public static final String ROUTING_KEY_EMAIL = "probend.resolucao.email.routingkey";

    // 1. Declara a fila
    @Bean
    public Queue emailQueue() {
        return new Queue(QUEUE_EMAIL, true); // true = fila persistente, sobrevive ao restart/queda
    }

    // 2. Declara o tópico (exchange), entrada onde o produtor publica mensagens
    // TopicExchange permite roteamento baseado em padrão, pode usar regras
    // como: * (um caractere), # (zero ou mais caracteres) para decidir
    // quais filas envia
    @Bean
    public TopicExchange probendExchange() {
        return new TopicExchange(EXCHANGE_PROBEND);
    }

    // 3. Declara o binding, vincula a fila ao tópico com uma chave de roteamento
    // A chave "probend.resolucao.email.routingkey" garante que apenas mensagens
    // com essa chave sejam entregues na fila
    @Bean
    public Binding bindingEmail(Queue emailQueue, TopicExchange probendExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(probendExchange)
                .with(ROUTING_KEY_EMAIL);
    }

    // 4. Bean de conversão (transforma POJOs em JSON e vice-versa)
    // Por padrão, o Spring AMQP usa a serialização padrão do Java,
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
