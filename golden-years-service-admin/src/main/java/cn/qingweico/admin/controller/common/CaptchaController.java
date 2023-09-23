package cn.qingweico.admin.controller.common;

import cn.hutool.core.codec.Base64;
import cn.qingweico.enums.CaptchaCodeType;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.util.CollUtils;
import cn.qingweico.util.redis.RedisCache;
import cn.qingweico.util.uuid.IdUtils;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 *
 * @author zqw
 * @data 2023-09-23
 */
@RestController
public class CaptchaController {
    private final Producer captchaProducer;
    private final Producer captchaProducerMath;
    private final RedisCache redisCache;

    @Autowired
    public CaptchaController(
            @Qualifier("captchaProducer")
            Producer captchaProducer,
            @Qualifier("captchaProducerMath")
            Producer captchaProducerMath,
            RedisCache redisCache) {
        this.captchaProducer = captchaProducer;
        this.captchaProducerMath = captchaProducerMath;
        this.redisCache = redisCache;
    }

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public Result getCode(HttpServletResponse response) throws IOException {
        boolean captchaEnabled = true;
        Map<String, Object> map = new HashMap<>(CollUtils.mapSize(2));
        map.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled) {
            return Result.ok(map);
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUuid();
        String verifyKey = RedisConst.CAPTCHA_CODE_KEY + uuid;

        String capStr, code;
        BufferedImage image;

        // TODO 验证码类型
        String captchaType = "math";
        if (CaptchaCodeType.MATH.getVal().equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisCache.setCacheObject(verifyKey, code, SysConst.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }

        map.put("uuid", uuid);
        map.put("img", Base64.encode(os.toByteArray()));
        return Result.ok(map);
    }
}
