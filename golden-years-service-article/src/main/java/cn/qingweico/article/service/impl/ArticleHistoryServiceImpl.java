package cn.qingweico.article.service.impl;

import cn.qingweico.core.service.BaseService;
import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.mapper.HistoryMapper;
import cn.qingweico.article.service.ArticleHistoryService;
import cn.qingweico.enums.ArticleHistoryDeleteType;
import cn.qingweico.global.SysConst;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.History;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2022/5/6
 */
@Slf4j
@Service
public class ArticleHistoryServiceImpl extends BaseService implements ArticleHistoryService {

    @Resource
    private HistoryMapper historyMapper;
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public PageInfo<History> getHistoryList(String userId, Integer pageNum, Integer pageSize) {
        Example example = new Example(History.class);
        example.orderBy(SysConst.CREATE_TIME).desc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SysConst.USER_ID, userId);
        PageHelper.startPage(pageNum, pageSize);
        List<History> list = historyMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void addHistory(String userId, String articleId) {
        History history = new History();
        history.setArticleId(articleId);
        History one = historyMapper.selectOne(history);
        // 用户已经浏览过该文章
        if (one != null) {
            one.setCreateTime(new Date());
            if (historyMapper.updateByPrimaryKeySelective(one) <= 0) {
                log.error("update history error");
            }
        } else {
            one = new History();
            String id = "";
            one.setId(id);
            one.setUserId(userId);
            one.setArticleId(articleId);
            one.setCreateTime(new Date());
            if (historyMapper.insert(one) <= 0) {
                log.error("insert history error");
            }
        }
    }

    @Override
    public void deleteHistory(String userId, Integer deleteModel) {
        Example example = new Example(History.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SysConst.USER_ID, userId);
        if (deleteModel != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            // 清理一个小时内文章浏览记录
            if (ArticleHistoryDeleteType.PAST_AN_HOUR.type.equals(deleteModel)) {
                c.add(Calendar.HOUR_OF_DAY, -1);
            } else if (ArticleHistoryDeleteType.PAST_AN_DAY.type.equals(deleteModel)) {
                // 清理一天内文章浏览记录
                c.add(Calendar.DAY_OF_WEEK, -1);
            } else if (ArticleHistoryDeleteType.PAST_THREE_DAY.type.equals(deleteModel)) {
                // 清理三天内文章浏览记录
                c.add(Calendar.DAY_OF_WEEK, -3);
            } else if (ArticleHistoryDeleteType.PAST_AN_WEEK.type.equals(deleteModel)) {
                // 清理一周内文章浏览记录
                c.add(Calendar.DAY_OF_WEEK, -7);
            }
            criteria.andGreaterThanOrEqualTo(SysConst.CREATE_TIME, c.getTime());
        }
        int deleteNum = historyMapper.deleteByExample(example);
        log.info("delete history, userId: {}, deleteModel: {}, deleteNum: {}", userId, deleteModel, deleteNum);
    }

    @Override
    public Article queryArticleById(String id) {
        return articleMapper.selectByPrimaryKey(id);
    }
}
