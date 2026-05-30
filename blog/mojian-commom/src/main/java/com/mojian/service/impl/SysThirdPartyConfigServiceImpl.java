package com.mojian.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.mojian.mapper.SysThirdPartyConfigMapper;
import com.mojian.entity.SysThirdPartyConfig;
import com.mojian.service.SysThirdPartyConfigService;
import com.mojian.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 第三方登录配置表 服务实现类
 * 仅支持查询和修改，不允许新增和删除（固定支持 Gitee / GitHub / QQ 三种登录方式）
 */
@Service
@RequiredArgsConstructor
public class SysThirdPartyConfigServiceImpl extends ServiceImpl<SysThirdPartyConfigMapper, SysThirdPartyConfig>
        implements SysThirdPartyConfigService {

    /**
     * 查询第三方登录配置分页列表
     */
    @Override
    public IPage<SysThirdPartyConfig> selectPage(SysThirdPartyConfig query) {
        LambdaQueryWrapper<SysThirdPartyConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getConfigName()), SysThirdPartyConfig::getConfigName, query.getConfigName());
        wrapper.eq(query.getStatus() != null, SysThirdPartyConfig::getStatus, query.getStatus());
        wrapper.eq(StringUtils.isNotBlank(query.getConfigSource()), SysThirdPartyConfig::getConfigSource, query.getConfigSource());
        wrapper.orderByAsc(SysThirdPartyConfig::getSort);
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 根据configKey获取配置（用于AuthServiceImpl构建AuthRequest）
     */
    @Override
    public SysThirdPartyConfig getByConfigKey(String configKey) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysThirdPartyConfig>()
                .eq(SysThirdPartyConfig::getConfigKey, configKey)
                .eq(SysThirdPartyConfig::getStatus, 1));
    }

    /**
     * 获取已启用的前台第三方登录配置列表（公开接口，脱敏返回）
     */
    @Override
    public List<SysThirdPartyConfig> getFrontEnabledList() {
        List<SysThirdPartyConfig> list = list(new LambdaQueryWrapper<SysThirdPartyConfig>()
                .eq(SysThirdPartyConfig::getConfigSource, "front")
                .eq(SysThirdPartyConfig::getStatus, 1)
                .orderByAsc(SysThirdPartyConfig::getSort));
        // 脱敏：只返回前端需要的字段
        list.forEach(item -> {
            item.setAppId(null);
            item.setAppSecret(null);
            item.setRedirectUrl(null);
        });
        return list;
    }

    /**
     * 获取已启用的后台第三方登录配置列表（公开接口，脱敏返回）
     */
    @Override
    public List<SysThirdPartyConfig> getAdminEnabledList() {
        List<SysThirdPartyConfig> list = list(new LambdaQueryWrapper<SysThirdPartyConfig>()
                .eq(SysThirdPartyConfig::getConfigSource, "admin")
                .eq(SysThirdPartyConfig::getStatus, 1)
                .orderByAsc(SysThirdPartyConfig::getSort));
        // 脱敏：只返回前端需要的字段
        list.forEach(item -> {
            item.setAppId(null);
            item.setAppSecret(null);
            item.setRedirectUrl(null);
        });
        return list;
    }

    /**
     * 修改第三方登录配置
     */
    @Override
    public boolean update(SysThirdPartyConfig config) {
        return updateById(config);
    }
}
