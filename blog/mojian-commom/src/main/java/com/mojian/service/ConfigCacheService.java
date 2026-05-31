package com.mojian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mojian.common.RedisConstants;
import com.mojian.entity.SysConfig;
import com.mojian.entity.SysWebConfig;
import com.mojian.mapper.SysConfigMapper;
import com.mojian.mapper.SysWebConfigMapper;
import com.mojian.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 配置缓存服务
 * 提供站点URL、系统参数等配置的缓存访问，减少数据库查询
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigCacheService {

    private final SysConfigMapper sysConfigMapper;
    private final SysWebConfigMapper sysWebConfigMapper;
    private final RedisUtil redisUtil;

    /**
     * 根据configKey获取配置值（带Redis缓存）
     *
     * @param configKey 配置键名
     * @return 配置值，不存在返回null
     */
    public String getConfigValue(String configKey) {
        String cacheKey = RedisConstants.SITE_CONFIG_KEY + configKey;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return cached.toString();
        }
        SysConfig config = sysConfigMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, configKey));
        if (config != null) {
            redisUtil.set(cacheKey, config.getConfigValue(),
                    RedisConstants.DAY_EXPIRE, TimeUnit.SECONDS);
            return config.getConfigValue();
        }
        return null;
    }

    /**
     * 获取后台站点基础URL
     */
    public String getAdminBaseUrl() {
        String url = getConfigValue("admin_base_url");
        return url != null ? url : "https://blog.jianyue.cloud";
    }

    /**
     * 获取前台站点基础URL
     */
    public String getFrontBaseUrl() {
        String url = getConfigValue("front_base_url");
        return url != null ? url : "https://gp.jianyue.cloud";
    }

    /**
     * 获取网站配置（带Redis缓存）
     * 供 EmailUtil 等需要站点信息的场景复用
     *
     * @return SysWebConfig，不存在返回null
     */
    public SysWebConfig getWebConfig() {
        Object value = redisUtil.get(RedisConstants.WEB_CONFIG_KEY);
        if (value != null) {
            return com.alibaba.fastjson2.JSONObject.parseObject(value.toString(), SysWebConfig.class);
        }
        SysWebConfig webConfig = sysWebConfigMapper.selectOne(
                new LambdaQueryWrapper<SysWebConfig>().last("limit 1"));
        if (webConfig != null) {
            redisUtil.set(RedisConstants.WEB_CONFIG_KEY,
                    com.alibaba.fastjson2.JSONObject.toJSONString(webConfig),
                    RedisConstants.DAY_EXPIRE, TimeUnit.SECONDS);
        }
        return webConfig;
    }

    /**
     * 清除指定configKey的缓存
     */
    public void evictConfigCache(String configKey) {
        redisUtil.delete(RedisConstants.SITE_CONFIG_KEY + configKey);
    }
}
