package com.mojian.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mojian.common.RedisConstants;
import com.mojian.service.FriendService;
import com.mojian.entity.SysFriend;
import com.mojian.enums.FriendStatusEnum;
import com.mojian.exception.ServiceException;
import com.mojian.mapper.SysFriendMapper;
import com.mojian.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final SysFriendMapper friendMapper;
    private final RedisUtil redisUtil;

    @Override
    public List<SysFriend> getFriendList() {
        // 优先从缓存获取
        Object cached = redisUtil.get(RedisConstants.FRIEND_LIST_KEY);
        if (cached != null) {
            return JSONObject.parseObject(cached.toString(),
                    new TypeReference<List<SysFriend>>() {});
        }
        List<SysFriend> result = friendMapper.selectList(new LambdaQueryWrapper<SysFriend>()
                .select(SysFriend::getId,SysFriend::getName,SysFriend::getInfo,SysFriend::getAvatar
                        ,SysFriend::getUrl)
                .eq(SysFriend::getStatus, FriendStatusEnum.UP.getCode())
                .orderByAsc(SysFriend::getSort));
        redisUtil.set(RedisConstants.FRIEND_LIST_KEY,
                JSONObject.toJSONString(result),
                RedisConstants.DAY_EXPIRE, TimeUnit.SECONDS);
        return result;
    }

    @Override
    public Boolean apply(SysFriend sysFriend) {
        SysFriend obj = friendMapper.selectOne(new LambdaQueryWrapper<SysFriend>()
                .eq(SysFriend::getUrl, sysFriend.getUrl()));
        if (ObjectUtils.isNotEmpty(obj)) {
            throw new ServiceException("申请友链失败，该网站已存在");
        }

        sysFriend.setStatus(FriendStatusEnum.APPLY.getCode());
        friendMapper.insert(sysFriend);

        return true;
    }
}
