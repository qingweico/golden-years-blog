package cn.qingweico.article.service.impl;

import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.mapper.HistoryMapper;
import cn.qingweico.article.service.ArticleHistoryService;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.entity.Article;
import cn.qingweico.entity.History;
import cn.qingweico.entity.model.ArticleHistory;
import cn.qingweico.entity.model.UserBasicInfo;
import cn.qingweico.enums.ArticleHistoryDeleteType;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    private GenericUserChannel guc;
    @Override
    public PagedResult getHistoryList(String userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<History> lwq = new LambdaQueryWrapper<>();
        lwq.orderByDesc(History::getCreateTime);
        lwq.eq(History::getUserId, userId);
        Page<History> page = new Page<>(pageNum,pageSize);
        // 分页查询
        IPage<History> iPage = historyMapper.selectPage(page, lwq);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotalPage(iPage.getPages());
        pagedResult.setTotalNumber(iPage.getTotal());
        pagedResult.setCurrentPage(iPage.getCurrent());
        List<History> records = iPage.getRecords();
        List<ArticleHistory> result = new ArrayList<>();
        for(History history : records) {
            String articleId = history.getArticleId();
            Article article = queryArticleById(articleId);
            ArticleHistory articleHistory = new ArticleHistory();
            articleHistory.setArticleName(article.getTitle());
            BeanUtils.copyProperties(history, articleHistory);
            UserBasicInfo userBasicInfo = guc.getUserBasicInfoClient(history.getUserId());
            articleHistory.setUsername(userBasicInfo.getNickname());
        }
        pagedResult.setRows(result);
        return pagedResult;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void addHistory(String userId, String articleId) {
        LambdaQueryWrapper<History> lwq = new LambdaQueryWrapper<>();
        lwq.eq(History::getArticleId, articleId);
        History one = historyMapper.selectOne(lwq);
        // 用户已经浏览过该文章
        if (one != null) {
            one.setCreateTime(DateUtils.nowDateTime());
            historyMapper.updateById(one);
        } else {
            one = new History();
            String id = SnowflakeIdWorker.nextId();
            one.setId(id);
            one.setUserId(userId);
            one.setArticleId(articleId);
            one.setCreateTime(DateUtils.nowDateTime());
            historyMapper.insert(one);
        }
    }

    @Override
    public void deleteHistory(String userId, Integer deleteModel) {
        LambdaQueryWrapper<History> lwq = new LambdaQueryWrapper<>();
        lwq.eq(History::getUserId, userId);
        if (deleteModel != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            // 清理一个小时内文章浏览记录
            if (ArticleHistoryDeleteType.PAST_AN_HOUR.getVal().equals(deleteModel)) {
                c.add(Calendar.HOUR_OF_DAY, -1);
            } else if (ArticleHistoryDeleteType.PAST_AN_DAY.getVal().equals(deleteModel)) {
                // 清理一天内文章浏览记录
                c.add(Calendar.DAY_OF_WEEK, -1);
            } else if (ArticleHistoryDeleteType.PAST_THREE_DAY.getVal().equals(deleteModel)) {
                // 清理三天内文章浏览记录
                c.add(Calendar.DAY_OF_WEEK, -3);
            } else if (ArticleHistoryDeleteType.PAST_AN_WEEK.getVal().equals(deleteModel)) {
                // 清理一周内文章浏览记录
                c.add(Calendar.DAY_OF_WEEK, -7);
            }
            lwq.ge(History::getCreateTime, c.getTime());
        }
        historyMapper.delete(lwq);
    }

    @Override
    public Article queryArticleById(String id) {
        return articleMapper.selectById(id);
    }
}
