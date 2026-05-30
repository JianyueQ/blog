package com.mojian.service;

import com.mojian.entity.SysUserThirdParty;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户第三方账号绑定表 服务接口
 */
public interface SysUserThirdPartyService extends IService<SysUserThirdParty> {

    /**
     * 根据第三方类型和第三方用户ID查询绑定关系
     */
    SysUserThirdParty getByThirdParty(String thirdPartyType, String thirdPartyId);

    /**
     * 根据用户ID查询绑定的第三方账号列表
     */
    List<SysUserThirdParty> listByUserId(Integer userId);

    /**
     * 绑定第三方账号
     */
    boolean bind(Integer userId, String thirdPartyType, String thirdPartyId,
                 String thirdPartyNickname, String thirdPartyAvatar);

    /**
     * 解绑第三方账号
     */
    boolean unbind(Integer userId, String thirdPartyType);
}
