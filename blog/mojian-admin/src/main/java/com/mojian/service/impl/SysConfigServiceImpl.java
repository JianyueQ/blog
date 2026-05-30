package com.mojian.service.impl;

import java.util.List;

import com.mojian.common.Constants;
import com.mojian.common.RedisConstants;
import com.mojian.exception.ServiceException;
import com.mojian.utils.RedisUtil;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.mojian.mapper.SysConfigMapper;
import com.mojian.entity.SysConfig;
import com.mojian.service.SysConfigService;
import com.mojian.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * 参数配置表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private final RedisUtil redisUtil;

    /**
     * 查询参数配置表分页列表
     */
    @Override
    public IPage<SysConfig> selectPage(SysConfig sysConfig) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.like(sysConfig.getConfigName() != null, SysConfig::getConfigName, sysConfig.getConfigName());
        wrapper.eq(sysConfig.getConfigType() != null, SysConfig::getConfigType, sysConfig.getConfigType());
        wrapper.eq(sysConfig.getConfigType() != null, SysConfig::getConfigType, sysConfig.getConfigType());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询参数配置表列表
     */
    @Override
    public List<SysConfig> selectList(SysConfig sysConfig) {
        return list(new QueryWrapper<>());
    }

    /**
     * 新增参数配置表
     */
    @Override
    public boolean insert(SysConfig sysConfig) {
        SysConfig obj = baseMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, sysConfig.getConfigKey()));
        if (obj != null) {
            throw new ServiceException("参数键名已存在");
        }
        return save(sysConfig);
    }

    /**
     * 修改参数配置表
     */
    @Override
    @CachePut(cacheNames = Constants.CACHE_SYS_CONFIG, key = "#sysConfig.configKey")
    public SysConfig update(SysConfig sysConfig) {
        SysConfig obj = baseMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, sysConfig.getConfigKey()));
        if (obj != null && !obj.getId().equals(sysConfig.getId())) {
            throw new ServiceException("参数键名已存在");
        }
        updateById(sysConfig);
        // 如果是邮件相关配置，清除邮件缓存
        if (sysConfig.getConfigKey().startsWith("mail_")) {
            redisUtil.delete(RedisConstants.EMAIL_CONFIG_KEY);
        }
        return sysConfig;
    }

    /**
     * 批量删除参数配置表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    @Cacheable(cacheNames = Constants.CACHE_SYS_CONFIG, key = "#key")
    public SysConfig selectConfigByKey(String key) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key));
    }
}
