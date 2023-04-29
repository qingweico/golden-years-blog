package cn.qingweico.article;

import cn.qingweico.core.config.RabbitMqDelayConfig;
import cn.qingweico.article.service.ArticleService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zqw
 * @date 2021/9/15
 */
@Component
public class RabbitMqDelayConsumer {

   @Resource
   private ArticleService articleService;

   @RabbitListener(queues = {RabbitMqDelayConfig.QUEUE_DELAY})
   public void listenQueue(String payLoad) {
      articleService.timelyPublishArticle(payLoad);
   }
}
