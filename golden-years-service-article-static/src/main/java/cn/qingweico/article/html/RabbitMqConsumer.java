package cn.qingweico.article.html;

import cn.qingweico.api.config.RabbitMqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zqw
 * @date 2021/9/14
 */
@Component
public class RabbitMqConsumer {


    @Resource
    private ArticleHtmlComponent articleHtmlComponent;

    @RabbitListener(queues = {RabbitMqConfig.QUEUE_ARTICLE})
    public void listenQueue(String payLoad, Message message) {

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if ("article.download.do".equals(routingKey)) {
            String articleId = payLoad.split(",")[0];
            String articleMongoId = payLoad.split(",")[1];
            try {
                articleHtmlComponent.download(articleId, articleMongoId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("article.delete.do".equals(routingKey)) {
            try {
                articleHtmlComponent.delete(payLoad);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("不符合的规则:" + routingKey);
        }

    }
}
