package com.mojian.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.mojian.common.RedisConstants;
import com.mojian.entity.SysArticle;
import com.mojian.entity.SysCategory;
import com.mojian.entity.SysNotifications;
import com.mojian.service.ArticleService;
import com.mojian.utils.IpUtil;
import com.mojian.utils.NotificationsUtil;
import com.mojian.utils.RedisUtil;
import com.mojian.vo.article.ArchiveListVo;
import com.mojian.vo.article.ArticleDetailVo;
import com.mojian.vo.article.ArticleListVo;
import com.mojian.vo.article.CategoryListVo;
import com.mojian.vo.tag.TagListVo;
import com.mojian.mapper.SysArticleMapper;
import com.mojian.mapper.SysCategoryMapper;
import com.mojian.mapper.SysTagMapper;
import com.mojian.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final SysArticleMapper sysArticleMapper;

    private final SysCategoryMapper sysCategoryMapper;

    private final SysTagMapper sysTagMapper;

    private final RedisUtil redisUtil;

    private final NotificationsUtil notificationsUtil;

    @Override
    public IPage<ArticleListVo> getArticleList(Integer tagId, Integer categoryId, String keyword) {
        IPage<ArticleListVo> page = sysArticleMapper.getArticleListApi(PageUtil.getPage(), tagId, categoryId, keyword);
        // 批量查询标签，解决N+1问题
        List<ArticleListVo> records = page.getRecords();
        if (!records.isEmpty()) {
            List<Long> articleIds = records.stream().map(ArticleListVo::getId).collect(Collectors.toList());
            List<TagListVo> allTags = sysTagMapper.getTagsByArticleIds(articleIds);
            Map<Long, List<TagListVo>> tagMap = allTags.stream()
                    .filter(t -> t.getArticleId() != null)
                    .collect(Collectors.groupingBy(TagListVo::getArticleId));
            records.forEach(a -> a.setTags(tagMap.getOrDefault(a.getId(), Collections.emptyList())));
        }
        return page;
    }

    @Override
    public ArticleDetailVo getArticleDetail(Long id) {
        ArticleDetailVo detailVo = sysArticleMapper.getArticleDetail(id);
        // 判断是否点赞
        Object userId = StpUtil.getLoginIdDefaultNull();
        if (userId != null) {
            detailVo.setIsLike(sysArticleMapper.getUserIsLike(id, Integer.parseInt(userId.toString())));
        }

        //添加阅读量
        String ip = IpUtil.getIp();
        ThreadUtil.execAsync(() -> {
            Map<Object, Object> map = redisUtil.hGetAll(RedisConstants.ARTICLE_QUANTITY);
            List<String> ipList = (List<String>) map.get(id.toString());
            if (ipList != null) {
                if (!ipList.contains(ip)) {
                    ipList.add(ip);
                }
            } else {
                ipList = new ArrayList<>();
                ipList.add(ip);
            }
            map.put(id.toString(), ipList);
            redisUtil.hSetAll(RedisConstants.ARTICLE_QUANTITY, map);
        });
        return detailVo;
    }

    @Override
    public List<ArchiveListVo> getArticleArchive() {
        // 一次查出所有已发布文章，在内存中按年份分组，避免N+1查询
        List<ArticleListVo> allArticles = sysArticleMapper.getAllPublishedArticlesForArchive();
        TreeMap<Integer, List<ArticleListVo>> grouped = allArticles.stream()
                .filter(a -> a.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getCreateTime().getYear(),
                        TreeMap::new,
                        Collectors.toList()));
        // TreeMap默认升序，descendingMap()降序遍历
        List<ArchiveListVo> list = new ArrayList<>();
        grouped.descendingMap().forEach((year, articles) ->
                list.add(new ArchiveListVo(year, articles)));
        return list;
    }

    @Override
    public List<CategoryListVo> getArticleCategories() {
        // 优先从缓存获取
        Object cached = redisUtil.get(RedisConstants.CATEGORY_ARTICLE_COUNT_KEY);
        if (cached != null) {
            return JSONObject.parseObject(cached.toString(),
                    new TypeReference<List<CategoryListVo>>() {});
        }
        List<CategoryListVo> result = sysCategoryMapper.getArticleCategories();
        redisUtil.set(RedisConstants.CATEGORY_ARTICLE_COUNT_KEY,
                JSONObject.toJSONString(result),
                RedisConstants.DAY_EXPIRE, TimeUnit.SECONDS);
        return result;
    }

    @Override
    public List<ArticleListVo> getCarouselArticle() {
        return getArticlesByCondition(SysArticle::getIsCarousel);
    }

    @Override
    public List<ArticleListVo> getRecommendArticle() {
        return getArticlesByCondition(SysArticle::getIsRecommend);
    }

    @Override
    public Boolean like(Long articleId) {
        // 判断是否点赞
        int userId = StpUtil.getLoginIdAsInt();
        Boolean isLike = sysArticleMapper.getUserIsLike(articleId, userId);
        if (isLike) {
            // 点过则取消点赞
            sysArticleMapper.unLike(articleId, userId);
        } else {
            sysArticleMapper.like(articleId, userId);
            ThreadUtil.execAsync(() -> {
                //发送通知事件
                SysNotifications notifications = SysNotifications.builder()
                        .title("文章点赞通知")
                        .articleId(articleId)
                        .isRead(0)
                        .type("like")
                        .fromUserId(StpUtil.getLoginIdAsLong())
                        .build();
                notificationsUtil.publish(notifications);
            });
        }
        return true;
    }

    @Override
    public List<SysCategory> getCategoryAll() {
        // 优先从缓存获取
        Object cached = redisUtil.get(RedisConstants.CATEGORY_LIST_KEY);
        if (cached != null) {
            return JSONObject.parseObject(cached.toString(),
                    new TypeReference<List<SysCategory>>() {});
        }
        List<SysCategory> result = sysCategoryMapper.selectList(new LambdaQueryWrapper<SysCategory>()
                .orderByAsc(SysCategory::getSort));
        redisUtil.set(RedisConstants.CATEGORY_LIST_KEY,
                JSONObject.toJSONString(result),
                RedisConstants.DAY_EXPIRE, TimeUnit.SECONDS);
        return result;
    }

    private List<ArticleListVo> getArticlesByCondition(SFunction<SysArticle, Object> conditionField) {
        LambdaQueryWrapper<SysArticle> wrapper = new LambdaQueryWrapper<SysArticle>()
                .select(SysArticle::getId, SysArticle::getTitle, SysArticle::getCover, SysArticle::getCreateTime)
                .orderByDesc(SysArticle::getCreateTime)
                .eq(conditionField, 1)
                .last("LIMIT 10");

        List<SysArticle> sysArticles = sysArticleMapper.selectList(wrapper);

        if (sysArticles == null || sysArticles.isEmpty()) {
            return Collections.emptyList();
        }

        return sysArticles.stream().map(item -> ArticleListVo.builder()
                .id(item.getId())
                .cover(item.getCover())
                .title(item.getTitle())
                .createTime(item.getCreateTime())
                .build()).collect(Collectors.toList());
    }
}
