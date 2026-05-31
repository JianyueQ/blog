package com.mojian.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mojian.common.Constants;
import com.mojian.common.RedisConstants;
import com.mojian.common.Result;
import com.mojian.entity.SysNotice;
import com.mojian.mapper.SysNoticeMapper;
import com.mojian.service.HomeService;
import com.mojian.entity.SysWebConfig;
import com.mojian.mapper.SysWebConfigMapper;
import com.mojian.utils.IpUtil;
import com.mojian.utils.RedisUtil;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final SysWebConfigMapper sysWebConfigMapper;

    private final RedisUtil redisUtil;

    private final SysNoticeMapper noticeMapper;

    @Override
    public Result<SysWebConfig> getWebConfig() {

        SysWebConfig sysWebConfig = new SysWebConfig();
        Object value = redisUtil.get(RedisConstants.WEB_CONFIG_KEY);
        if (value == null) {
            LambdaQueryWrapper<SysWebConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.last("limit 1");
            sysWebConfig = sysWebConfigMapper.selectOne(wrapper);
            // 查询后写回缓存，过期时间7天
            if (sysWebConfig != null) {
                redisUtil.set(RedisConstants.WEB_CONFIG_KEY,
                        JSONObject.toJSONString(sysWebConfig),
                        RedisConstants.WEEK_EXPIRE, TimeUnit.SECONDS);
            }
        } else {
            sysWebConfig = JSONObject.parseObject(value.toString(), SysWebConfig.class);
        }

        //获取浏览量和访问量（直接get判空，避免hasKey+get两次Redis往返）
        long blogViewsCount = 0;
        long visitorCount = 0;
        Object viewsObj = redisUtil.get(RedisConstants.BLOG_VIEWS_COUNT);
        if (viewsObj != null) {
            blogViewsCount = Long.parseLong(viewsObj.toString());
        }
        Object visitorObj = redisUtil.get(RedisConstants.UNIQUE_VISITOR_COUNT);
        if (visitorObj != null) {
            visitorCount = Long.parseLong(visitorObj.toString());
        }

        return Result.success(sysWebConfig).putExtra("blogViewsCount", blogViewsCount).putExtra("visitorCount", visitorCount);
    }

    @Override
    public JSONObject getHotSearch(String type) {
        // 优先从缓存获取（热搜数据缓存30分钟，避免每次请求都调外部HTTP）
        String cacheKey = RedisConstants.HOT_SEARCH_KEY + type;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return JSONObject.parseObject(cached.toString());
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("access-key", "f94be500c45148bc185be24a38c04ad3");
        paramMap.put("secret-key", "27563ca627d5db0d57e831ca4de0f75f");
        String url = "https://www.coderutil.com/api/resou/v1/" + type;
        String result = HttpUtil.get(url, paramMap);
        JSONObject jsonResult = JSONObject.parseObject(result);
        // 缓存30分钟
        if (jsonResult != null) {
            redisUtil.set(cacheKey, result, RedisConstants.HALF_HOUR_EXPIRE, TimeUnit.SECONDS);
        }
        return jsonResult;
    }

    @Override
    public void report() {
        // 获取ip
        String ipAddress = IpUtil.getIp();
        // 通过浏览器解析工具类UserAgent获取访问设备信息
        UserAgent userAgent = IpUtil.getUserAgent(Objects.requireNonNull(IpUtil.getRequest()));
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        // 生成唯一用户标识
        String uuid = ipAddress + browser.getName() + operatingSystem.getName();
        String md5 = DigestUtils.md5DigestAsHex(uuid.getBytes());
        // 判断是否访问
        if (!redisUtil.sIsMember(RedisConstants.UNIQUE_VISITOR, md5)) {
            // 访客量+1
            redisUtil.increment(RedisConstants.UNIQUE_VISITOR_COUNT, 1);
            // 保存唯一标识
            redisUtil.sAdd(RedisConstants.UNIQUE_VISITOR, md5);
        }
        // 访问量+1
        redisUtil.increment(RedisConstants.BLOG_VIEWS_COUNT, 1);
    }

    @Override
    public Map<String, List<SysNotice>> getNotice() {
        // 优先从缓存获取
        Object cached = redisUtil.get(RedisConstants.NOTICE_LIST_KEY);
        if (cached != null) {
            return JSONObject.parseObject(cached.toString(),
                    new TypeReference<Map<String, List<SysNotice>>>() {});
        }
        List<SysNotice> sysNotices = noticeMapper.selectList(new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getIsShow, Constants.YES));
        Map<String, List<SysNotice>> result = sysNotices.stream()
                .collect(Collectors.groupingBy(SysNotice::getPosition));
        // 缓存1天
        redisUtil.set(RedisConstants.NOTICE_LIST_KEY,
                JSONObject.toJSONString(result),
                RedisConstants.DAY_EXPIRE, TimeUnit.SECONDS);
        return result;
    }
}
