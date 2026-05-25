package com.mojian.service;

import cn.hutool.core.lang.WeightRandom;
import com.mojian.common.RedisConstants;
import com.mojian.entity.CodeImage;
import com.mojian.mapper.CodeImageMapper;
import com.mojian.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 验证码图片缓存服务
 * <p>
 * 封装验证码图片库的随机选择逻辑，下沉到 mojian-commom 模块，
 * 供 CodeImageController 和 CaptchaUtil 直接调用。
 */
@Service
@RequiredArgsConstructor
public class CodeImageCacheService {

    private final RedisUtil redisUtil;
    private final CodeImageMapper codeImageMapper;

    /**
     * 随机获取一张验证码图片 URL
     * <p>
     * 使用 Hash + ZSet 缓存：
     * - Hash code:image:urls 存储 id -> url
     * - ZSet code:image:scores 存储 id -> score（出现次数）
     * <p>
     * 权重算法：weight = 1 / (score + 1)，score 越小权重越大
     */
    public String getRandomCodeImageUrl() {
        Set<ZSetOperations.TypedTuple<Object>> entries = redisUtil.zRangeWithScores(RedisConstants.CODE_IMAGE_SCORES_KEY, 0, -1);

        if (entries == null || entries.isEmpty()) {
            loadCacheFromDb();
            entries = redisUtil.zRangeWithScores(RedisConstants.CODE_IMAGE_SCORES_KEY, 0, -1);
        }

        if (entries == null || entries.isEmpty()) {
            return null;
        }

        String selectedId = weightRandomSelect(entries);
        if (selectedId == null) {
            return null;
        }

        Object url = redisUtil.hGet(RedisConstants.CODE_IMAGE_URLS_KEY, selectedId);
        return url != null ? url.toString() : null;
    }

    /**
     * Hutool WeightRandom：按反比权重随机选择
     */
    private String weightRandomSelect(Set<ZSetOperations.TypedTuple<Object>> entries) {
        List<WeightRandom.WeightObj<String>> weightList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> entry : entries) {
            String id = (String) entry.getValue();
            double score = entry.getScore() != null ? entry.getScore() : 0;
            double weight = 1.0 / (score + 1);
            weightList.add(new WeightRandom.WeightObj<>(id, weight));
        }

        WeightRandom<String> weightRandom = new WeightRandom<>(weightList);
        String selectedId = weightRandom.next();

        // 增加选中图片的 score
        redisUtil.zIncrementScore(RedisConstants.CODE_IMAGE_SCORES_KEY, selectedId, 1);

        return selectedId;
    }

    /**
     * 从数据库加载缓存到 Redis
     */
    private void loadCacheFromDb() {
        List<CodeImage> list = codeImageMapper.selectList(null);
        for (CodeImage image : list) {
            if (image.getId() != null && image.getUrl() != null) {
                redisUtil.hSet(RedisConstants.CODE_IMAGE_URLS_KEY, image.getId(), image.getUrl());
                if (Boolean.FALSE.equals(redisUtil.zIsMember(RedisConstants.CODE_IMAGE_SCORES_KEY, image.getId()))) {
                    redisUtil.zAdd(RedisConstants.CODE_IMAGE_SCORES_KEY, image.getId(), 0);
                }
            }
        }
    }
}
