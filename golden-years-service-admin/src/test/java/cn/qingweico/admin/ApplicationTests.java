package cn.qingweico.admin;

import cn.qingweico.admin.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zqw
 * @date 2023/9/30
 */
@SpringBootTest
class ApplicationTests {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Test
    void selectAll() {
        System.out.println(sysUserMapper.selectList(null));
    }
}
