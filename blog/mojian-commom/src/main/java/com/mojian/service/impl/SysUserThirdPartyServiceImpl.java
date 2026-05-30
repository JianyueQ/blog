package com.mojian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mojian.entity.SysThirdPartyConfig;
import com.mojian.entity.SysUserThirdParty;
import com.mojian.exception.ServiceException;
import com.mojian.mapper.SysUserThirdPartyMapper;
import com.mojian.service.SysThirdPartyConfigService;
import com.mojian.service.SysUserThirdPartyService;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
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

    private final SysThirdPartyConfigService sysThirdPartyConfigService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindByCode(Integer userId, String thirdPartyType, String code) {
        // 获取第三方配置
        SysThirdPartyConfig config = sysThirdPartyConfigService.getByConfigKey(thirdPartyType + "_admin");
        if (config == null) {
            throw new ServiceException("未配置" + thirdPartyType + "第三方登录");
        }

        // 构建 AuthRequest
        AuthRequest authRequest = buildAuthRequest(config);
        if (authRequest == null) {
            throw new ServiceException("不支持的第三方类型: " + thirdPartyType);
        }

        // 通过 code 获取第三方用户信息
        AuthCallback callback = new AuthCallback();
        callback.setCode(code);
        AuthResponse<AuthUser> response = authRequest.login(callback);
        if (!response.ok() || response.getData() == null) {
            throw new ServiceException("第三方授权失败: " + (response.getMsg() != null ? response.getMsg() : "未知错误"));
        }
        AuthUser authUser = (AuthUser) response.getData();

        // 执行绑定
        return bind(userId, thirdPartyType, authUser.getUuid(),
                authUser.getNickname(), authUser.getAvatar());
    }

    /**
     * 构建 AuthRequest（复用 AuthServiceImpl 的逻辑）
     */
    private AuthRequest buildAuthRequest(SysThirdPartyConfig config) {
        String source = config.getConfigKey().replace("_admin", "");
        AuthConfig authConfig = AuthConfig.builder()
                .clientId(config.getAppId())
                .clientSecret(config.getAppSecret())
                .redirectUri(config.getRedirectUrl())
                .build();
        return switch (source) {
            case "gitee" -> new AuthGiteeRequest(authConfig);
            case "github" -> new AuthGithubRequest(authConfig);
            case "qq" -> new AuthQqRequest(authConfig);
            default -> null;
        };
    }
}
