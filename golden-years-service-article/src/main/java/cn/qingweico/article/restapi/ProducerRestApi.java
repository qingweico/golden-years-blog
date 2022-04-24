package cn.qingweico.article.restapi;

import cn.qingweico.api.config.RabbitMqConfig;
import cn.qingweico.global.SysConf;
import cn.qingweico.result.GraceJsonResult;
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
 * @author zqw
 * @date 2021/9/14
 */
@RestController
@RequestMapping("producer")
public class ProducerRestApi {

    @Resource
    public ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("send")
    public GraceJsonResult send() {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_ARTICLE,
                "article.hello",
                "这是一条消息!");
        return GraceJsonResult.ok();
    }

    @GetMapping("delay")
    public GraceJsonResult delay() {
        System.out.println("生产者发送的延迟消息:" + new Date());
        return GraceJsonResult.ok();
    }

    @GetMapping("delete/{articleId}")
    public GraceJsonResult delete(@PathVariable("articleId") String articleId) {
        elasticsearchTemplate.delete(ArticleEo.class, articleId);
        return GraceJsonResult.ok();
    }
}
