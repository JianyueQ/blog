package com.mojian.service;

import com.mojian.entity.SysThirdPartyConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 第三方登录配置表 服务接口
 * 仅支持查询和修改，不允许新增和删除（固定支持 Gitee / GitHub / QQ 三种登录方式）
 */
public interface SysThirdPartyConfigService extends IService<SysThirdPartyConfig> {

    /**
     * 查询第三方登录配置分页列表
     */
    IPage<SysThirdPartyConfig> selectPage(SysThirdPartyConfig query);

    /**
     * 根据configKey获取配置（用于AuthServiceImpl构建AuthRequest）
     */
    SysThirdPartyConfig getByConfigKey(String configKey);

    /**
     * 获取已启用的第三方登录配置列表（公开接口，脱敏返回）
     */
    List<SysThirdPartyConfig> getEnabledList();

    /**
     * 修改第三方登录配置
     */
    boolean update(SysThirdPartyConfig config);
}
