package com.mojian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mojian.service.MessageService;
import com.mojian.entity.SysMessage;
import com.mojian.mapper.SysMessageMapper;
import com.mojian.utils.IpUtil;
import com.mojian.utils.SensitiveUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final SysMessageMapper messageMapper;

    @Override
    public List<SysMessage> getMessageList() {
        // 添加LIMIT限制，避免全表加载
        return messageMapper.selectList(new LambdaQueryWrapper<SysMessage>()
                .orderByDesc(SysMessage::getCreateTime)
                .last("LIMIT 200"));
    }

    @Override
    public Boolean add(SysMessage sysMessage) {
        String ip = IpUtil.getIp();
        sysMessage.setIp(ip);
        sysMessage.setSource(IpUtil.getIp2region(ip));
        sysMessage.setContent(SensitiveUtil.filter(sysMessage.getContent()));
        messageMapper.insert(sysMessage);
        return true;
    }
}
