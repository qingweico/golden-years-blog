package cn.qingweico.article;

import cn.qingweico.core.config.RabbitMqConfig;
import cn.qingweico.article.controller.ArticleDetailController;
import cn.qingweico.global.SysConst;
import cn.qingweico.pojo.bo.CollectBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zqw
 * @date 2022/4/20
 */
@Slf4j
@Component
public class RabbitMqConsumer {
    @Resource
    private ArticleDetailController articleDetailRestApi;
    @RabbitListener(queues = {RabbitMqConfig.QUEUE_ARTICLE})
    public void listenQueue(String payLoad, Message message) {
        log.info("payLoad: ---> {}, message: ---> {}", payLoad, message.toString());
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        // 为新用户创建默认收藏夹
        if (SysConst.ARTICLE_CREATE_FAVORITES_DO.equals(routingKey)) {
            String userId = payLoad.split(",")[0];
            try {
                CollectBO collectBO = new CollectBO();
                collectBO.setUserId(userId);
                articleDetailRestApi.createFavorites(collectBO);
            } catch (Exception e) {
                log.error("creat favorites error");
            }
        }else {
            log.error("不符合的规则: {}",routingKey);
        }
    }
}
