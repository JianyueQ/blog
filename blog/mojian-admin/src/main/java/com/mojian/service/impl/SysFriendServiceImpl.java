package com.mojian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mojian.common.RedisConstants;
import com.mojian.entity.SysFriend;
import com.mojian.mapper.SysFriendMapper;
import com.mojian.service.SysFriendService;
import com.mojian.utils.PageUtil;
import com.mojian.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SysFriendServiceImpl extends ServiceImpl<SysFriendMapper, SysFriend> implements SysFriendService {

    private final RedisUtil redisUtil;

    @Override
    public IPage<SysFriend> selectPage(SysFriend sysFriend) {
        LambdaQueryWrapper<SysFriend> wrapper = new LambdaQueryWrapper<SysFriend>()
                .eq(sysFriend.getName() != null, SysFriend::getName, sysFriend.getName())
                .eq(sysFriend.getStatus() != null, SysFriend::getStatus, sysFriend.getStatus());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 修改友情链接
     */
    @Override
    public boolean update(SysFriend sysFriend) {
        boolean result = updateById(sysFriend);
        redisUtil.delete(RedisConstants.FRIEND_LIST_KEY);
        return result;
    }
}
