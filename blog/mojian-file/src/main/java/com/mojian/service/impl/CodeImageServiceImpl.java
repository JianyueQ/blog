package com.mojian.service.impl;

import cn.hutool.core.lang.WeightRandom;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mojian.common.RedisConstants;
import com.mojian.entity.CodeImage;
import com.mojian.mapper.CodeImageMapper;
import com.mojian.service.CodeImageCacheService;
import com.mojian.service.CodeImageService;
import com.mojian.utils.PageUtil;
import com.mojian.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 验证码图片库 服务实现类
 * <p>
 * Redis缓存设计：
 * - Hash code:image:urls 存储 id -> url
 * - ZSet code:image:scores 存储 id -> score（出现次数）
 * <p>
 * 选择算法：Hutool WeightRandom 反比权重选择
 * - 权重 = 1 / (score + 1)，score越小权重越大
 * - 选中后score += 1
 */
@Service
@RequiredArgsConstructor
public class CodeImageServiceImpl extends ServiceImpl<CodeImageMapper, CodeImage> implements CodeImageService {

    private final RedisUtil redisUtil;
    private final CodeImageCacheService codeImageCacheService;
    @Override
    public IPage<CodeImage> selectPage(CodeImage codeImage) {
        LambdaQueryWrapper<CodeImage> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.like(codeImage.getFilename() != null, CodeImage::getFilename, codeImage.getFilename());
        wrapper.eq(codeImage.getSource() != null, CodeImage::getSource, codeImage.getSource());
        wrapper.orderByDesc(CodeImage::getCreateTime);
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 随机获取一张验证码图片URL
     * 委托给 CodeImageCacheService 处理
     */
    @Override
    public String getRandomCodeImageUrl() {
        return codeImageCacheService.getRandomCodeImageUrl();
    }

    /**
     * 新增验证码图片
     */
    @Override
    public boolean insert(CodeImage codeImage) {
        boolean result = save(codeImage);
        if (result && codeImage.getId() != null) {
            // Hash添加 id -> url
            redisUtil.hSet(RedisConstants.CODE_IMAGE_URLS_KEY, codeImage.getId(), codeImage.getUrl());
            // ZSet添加 id -> score=0（初始权重最大）
            redisUtil.zAdd(RedisConstants.CODE_IMAGE_SCORES_KEY, codeImage.getId(), 0);
        }
        return result;
    }

    /**
     * 修改验证码图片
     */
    @Override
    public boolean update(CodeImage codeImage) {
        boolean result = updateById(codeImage);
        if (result && codeImage.getId() != null && codeImage.getUrl() != null) {
            // 只更新Hash中的URL，不影响ZSet权重
            redisUtil.hSet(RedisConstants.CODE_IMAGE_URLS_KEY, codeImage.getId(), codeImage.getUrl());
        }
        return result;
    }

    /**
     * 批量删除验证码图片
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<String> ids) {
        boolean result = removeByIds(ids);
        if (result) {
            for (String id : ids) {
                // Hash删除
                redisUtil.hDel(RedisConstants.CODE_IMAGE_URLS_KEY, id);
                // ZSet删除
                redisUtil.zRemove(RedisConstants.CODE_IMAGE_SCORES_KEY, id);
            }
        }
        return result;
    }
}