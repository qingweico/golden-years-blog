package cn.qingweico.api.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zqw
 * @date 2021/9/15
 */
@Configuration
public class RabbitMqDelayConfig {
   public static final String EXCHANGE_DELAY = "exchange_delay";
   public static final String QUEUE_DELAY = "queue_delay";

   @Bean(EXCHANGE_DELAY)
   public Exchange exchange() {
      return ExchangeBuilder.topicExchange(EXCHANGE_DELAY)
              // 开启消息延迟
              .delayed()
              .durable(true)
              .build();
   }

   @Bean(QUEUE_DELAY)
   public Queue queue() {
      return new Queue(QUEUE_DELAY);
   }

   @Bean
   public Binding delayBinding(@Qualifier(EXCHANGE_DELAY) Exchange exchange,
                          @Qualifier(QUEUE_DELAY) Queue queue) {
      return BindingBuilder.bind(queue)
              .to(exchange)
              .with("article.delay.publish.*.do")
              .noargs();
   }
}
