package cn.qingweico.api.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author:qiming
 * @date: 2021/9/14
 */
@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_ARTICLE = "exchange_article";
    public static final String QUEUE_ARTICLE = "queue_article";

    @Bean(EXCHANGE_ARTICLE)
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_ARTICLE)
                .durable(true)
                .build();
    }

    @Bean(QUEUE_ARTICLE)
    public Queue queue() {
        return new Queue(QUEUE_ARTICLE);
    }

    @Bean
    public Binding binding(@Qualifier(EXCHANGE_ARTICLE) Exchange exchange,
                           @Qualifier(QUEUE_ARTICLE) Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("article.*.do")
                .noargs();
    }
}
