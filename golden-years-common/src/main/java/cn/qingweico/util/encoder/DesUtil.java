package cn.qingweico.util.encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * DES是一种对称加密算法
 * 所谓对称加密算法即: 加密和解密使用相同的密钥算法
 *
 * @author 周庆伟
 * @date 2020/10/12
 */

public class DesUtil {
    private final static Key KEY;
    /**
     * 设置密钥key
     */
    private final static String KEY_STR = "!@$#854%^&<<?;";
    private final static String CHARACTER = "UTF-8";
    private final static String ALGORITHM = "DES";

    static {
        try {
            //生成DES算法对象
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            //运用SHA1安全策略
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            //设置密钥种子
            secureRandom.setSeed(KEY_STR.getBytes());
            //初始化基于SHA1的算法对象
            generator.init(secureRandom);
            //生成密钥对象
            KEY = generator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * 获取加密后的信息
     *
     * @param target 待加密的信息
     * @return 密文
     */
    public static String getEncryptString(String target) {
        try {
            //按UTF8编码
            byte[] bytes = target.getBytes(CHARACTER);
            //获取加密对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            //初始化密码信息
            cipher.init(Cipher.ENCRYPT_MODE, KEY);
            //加密
            byte[] doFinal = cipher.doFinal(bytes);
            //将byte[] encode为String返回
            return Base64.getEncoder().encodeToString(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * 获取解密之后的信息
     *
     * @param des 待解密的信息
     * @return 明文
     */
    public static String getDecryptString(String des) {
        try {
            // 将字符串des转换为byte
            byte[] bytes = Base64.getDecoder().decode(des);
            // 获取解密对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化解密信息
            cipher.init(Cipher.DECRYPT_MODE, KEY);
            // 解密
            byte[] doFinal = cipher.doFinal(bytes);
            // 返回解密之后的信息
            return new String(doFinal, CHARACTER);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
}
