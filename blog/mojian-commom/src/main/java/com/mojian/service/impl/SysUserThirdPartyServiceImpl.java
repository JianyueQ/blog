package com.mojian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mojian.entity.SysUserThirdParty;
import com.mojian.exception.ServiceException;
import com.mojian.mapper.SysUserThirdPartyMapper;
import com.mojian.service.SysUserThirdPartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户第三方账号绑定表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserThirdPartyServiceImpl extends ServiceImpl<SysUserThirdPartyMapper, SysUserThirdParty>
        implements SysUserThirdPartyService {

    @Override
    public SysUserThirdParty getByThirdParty(String thirdPartyType, String thirdPartyId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysUserThirdParty>()
                .eq(SysUserThirdParty::getThirdPartyType, thirdPartyType)
                .eq(SysUserThirdParty::getThirdPartyId, thirdPartyId));
    }

    @Override
    public List<SysUserThirdParty> listByUserId(Integer userId) {
        return list(new LambdaQueryWrapper<SysUserThirdParty>()
                .eq(SysUserThirdParty::getUserId, userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bind(Integer userId, String thirdPartyType, String thirdPartyId,
                        String thirdPartyNickname, String thirdPartyAvatar) {
        // 检查该第三方账号是否已被其他用户绑定
        SysUserThirdParty existing = getByThirdParty(thirdPartyType, thirdPartyId);
        if (existing != null) {
            throw new ServiceException("该第三方账号已被其他用户绑定");
        }
        // 检查当前用户是否已绑定该类型
        SysUserThirdParty userBound = baseMapper.selectOne(new LambdaQueryWrapper<SysUserThirdParty>()
                .eq(SysUserThirdParty::getUserId, userId)
                .eq(SysUserThirdParty::getThirdPartyType, thirdPartyType));
        if (userBound != null) {
            throw new ServiceException("您已绑定" + thirdPartyType + "账号，请先解绑再重新绑定");
        }

        SysUserThirdParty binding = SysUserThirdParty.builder()
                .userId(userId)
                .thirdPartyType(thirdPartyType)
                .thirdPartyId(thirdPartyId)
                .thirdPartyNickname(thirdPartyNickname)
                .thirdPartyAvatar(thirdPartyAvatar)
                .build();
        return save(binding);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbind(Integer userId, String thirdPartyType) {
        return remove(new LambdaQueryWrapper<SysUserThirdParty>()
                .eq(SysUserThirdParty::getUserId, userId)
                .eq(SysUserThirdParty::getThirdPartyType, thirdPartyType));
    }
}
