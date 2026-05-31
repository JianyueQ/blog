package com.mojian.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.mojian.common.RedisConstants;
import com.mojian.service.TagService;
import com.mojian.utils.RedisUtil;
import com.mojian.vo.tag.TagListVo;
import com.mojian.mapper.SysTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final SysTagMapper sysTagMapper;
    private final RedisUtil redisUtil;

    @Override
    public List<TagListVo> getTagsApi() {
        // 优先从缓存获取
        Object cached = redisUtil.get(RedisConstants.TAG_LIST_KEY);
        if (cached != null) {
            return JSONObject.parseObject(cached.toString(),
                    new TypeReference<List<TagListVo>>() {});
        }
        List<TagListVo> result = sysTagMapper.getTagsApi();
        redisUtil.set(RedisConstants.TAG_LIST_KEY,
                JSONObject.toJSONString(result),
                RedisConstants.DAY_EXPIRE, TimeUnit.SECONDS);
        return result;
    }
}
