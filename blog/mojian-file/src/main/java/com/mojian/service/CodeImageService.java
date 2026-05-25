package com.mojian.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mojian.entity.CodeImage;

import java.util.List;

/**
 * 验证码图片库 服务接口
 */
public interface CodeImageService extends IService<CodeImage> {

    /**
     * 查询验证码图片分页列表
     */
    IPage<CodeImage> selectPage(CodeImage codeImage);

    /**
     * 随机获取一张验证码图片URL
     * 先从Redis缓存获取，没有则查询数据库并缓存
     */
    String getRandomCodeImageUrl();

    /**
     * 新增验证码图片
     */
    boolean insert(CodeImage codeImage);

    /**
     * 修改验证码图片
     */
    boolean update(CodeImage codeImage);

    /**
     * 批量删除验证码图片
     */
    boolean deleteByIds(List<String> ids);
}
