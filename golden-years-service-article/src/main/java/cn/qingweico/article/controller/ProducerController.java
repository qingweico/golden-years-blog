package cn.qingweico.article.controller;

import cn.qingweico.api.config.RabbitMqConfig;
import cn.qingweico.result.Result;
import cn.qingweico.pojo.eo.ArticleEo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 *
 * MQ test 生产消息
 * @author zqw
 * @date 2021/9/14
 */
@RestController
@RequestMapping("producer")
public class ProducerController {

    @Resource
    public ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("send")
    public Result send() {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_ARTICLE,
                "article.hello",
                "这是一条消息!");
        return Result.ok();
    }

    @GetMapping("delay")
    public Result delay() {
        System.out.println("生产者发送的延迟消息:" + new Date());
        return Result.ok();
    }

    @GetMapping("delete/{articleId}")
    public Result delete(@PathVariable("articleId") String articleId) {
        elasticsearchTemplate.delete(ArticleEo.class, articleId);
        return Result.ok();
    }
}
