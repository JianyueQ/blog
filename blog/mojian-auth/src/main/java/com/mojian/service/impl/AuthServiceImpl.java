package com.mojian.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mojian.common.Constants;
import com.mojian.common.RedisConstants;
import com.mojian.config.properties.WechatProperties;
import com.mojian.dto.Captcha;
import com.mojian.dto.EmailRegisterDto;
import com.mojian.dto.LoginDTO;
import com.mojian.dto.user.LoginUserInfo;
import com.mojian.entity.SysConfig;
import com.mojian.entity.SysRole;
import com.mojian.entity.SysThirdPartyConfig;
import com.mojian.entity.SysUserThirdParty;
import com.mojian.enums.LoginTypeEnum;
import com.mojian.mapper.SysConfigMapper;
import com.mojian.service.AuthService;
import com.mojian.service.SysThirdPartyConfigService;
import com.mojian.service.SysUserThirdPartyService;
import com.mojian.entity.SysUser;
import com.mojian.enums.MenuTypeEnum;
import com.mojian.exception.ServiceException;
import com.mojian.mapper.SysMenuMapper;
import com.mojian.mapper.SysRoleMapper;
import com.mojian.mapper.SysUserMapper;
import com.mojian.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.apache.commons.lang3.ObjectUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final SysUserMapper userMapper;

    private final SysRoleMapper roleMapper;

    private final SysMenuMapper menuMapper;

    private final EmailUtil emailUtil;

    private final RedisUtil redisUtil;

    private final SysUserMapper sysUserMapper;

    private final String[] avatarList = {
            "https://api.dicebear.com/6.x/pixel-art/svg?seed=Raccoon",
            "https://api.dicebear.com/6.x/pixel-art/svg?seed=Kitty",
            "https://api.dicebear.com/6.x/pixel-art/svg?seed=Puppy",
            "https://api.dicebear.com/6.x/pixel-art/svg?seed=Bunny",
            "https://api.dicebear.com/6.x/pixel-art/svg?seed=Fox"
    };
    private final SysRoleMapper sysRoleMapper;

    private final WechatProperties wechatProperties;

    private final SysConfigMapper sysConfigMapper;

    private final SysThirdPartyConfigService sysThirdPartyConfigService;

    private final SysUserThirdPartyService sysUserThirdPartyService;


    @Override
    public LoginUserInfo login(LoginDTO loginDTO) {

        SysConfig verifySwitch = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, "slider_verify_switch"));
        if (verifySwitch != null && verifySwitch.getConfigValue().equals("Y")) {
            //校验验证码
            CaptchaUtil.checkImageCode(loginDTO.getNonceStr(), loginDTO.getValue());
        }


        // 查询用户
        SysUser user = userMapper.selectByUsername(loginDTO.getUsername());

        //校验是否能够登录
        validateLogin(loginDTO, user);

        // 执行登录
        StpUtil.login(user.getId());
        String tokenValue = StpUtil.getTokenValue();

        // 返回用户信息
        LoginUserInfo loginUserInfo = BeanCopyUtil.copyObj(user, LoginUserInfo.class);
        loginUserInfo.setToken(tokenValue);

        StpUtil.getSession().set(Constants.CURRENT_USER, loginUserInfo);
        return loginUserInfo;
    }

    private static void validateLogin(LoginDTO loginDTO, SysUser user) {
        if (user == null) {
            throw new ServiceException("登录用户不存在");
        }
        //加密密码
        String password = BCrypt.hashpw(loginDTO.getPassword());
        // 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new ServiceException("用户名或密码错误");
        }

        // 验证状态
        if (user.getStatus() != 1) {
            throw new ServiceException("账号已被禁用");
        }

        if (user.getUsername().equals(Constants.TEST) && loginDTO.getSource().equalsIgnoreCase("PC")) {
            throw new ServiceException("演示用户不允许门户登录！");
        }
    }

    @Override
    public LoginUserInfo getLoginUserInfo(String source) {
        // 获取当前登录用户ID
        Integer userId = StpUtil.getLoginIdAsInt();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        LoginUserInfo loginUserInfo = BeanCopyUtil.copyObj(user, LoginUserInfo.class);

        //获取菜单权限列表
        if (source.equalsIgnoreCase(Constants.ADMIN)) {
            List<String> permissions;
            List<String> roles = roleMapper.selectRolesCodeByUserId(userId);
            if (roles.contains(Constants.ADMIN)) {
                permissions = menuMapper.getPermissionList(MenuTypeEnum.BUTTON.getCode());
            } else {
                permissions = menuMapper.getPermissionListByUserId(userId, MenuTypeEnum.BUTTON.getCode());
            }
            loginUserInfo.setRoles(roles);
            loginUserInfo.setPermissions(permissions);
        }

        return loginUserInfo;
    }

    @Override
    public Boolean sendEmailCode(String email) throws MessagingException {
        emailUtil.sendCode(email);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean register(EmailRegisterDto dto) {

        validateEmailCode(dto);

        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getEmail()));
        if (sysUser != null) {
            throw new ServiceException("当前邮箱已注册，请前往登录");
        }

        //获取随机头像
        String avatar = avatarList[(int) (ThreadLocalRandom.current().nextDouble() * avatarList.length)];
        sysUser = SysUser.builder()
                .username(dto.getEmail())
                .password(BCrypt.hashpw(dto.getPassword()))
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .avatar(avatar)
                .status(Constants.YES)
                .build();
        sysUserMapper.insert(sysUser);

        //添加用户角色信息
        insertRole(sysUser);

        redisUtil.delete(RedisConstants.CAPTCHA_CODE_KEY + dto.getEmail());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean forgot(EmailRegisterDto dto) {
        validateEmailCode(dto);
        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getEmail()));
        if (sysUser == null) {
            throw new ServiceException("当前邮箱未注册，请前往注册");
        }
        sysUser.setPassword(BCrypt.hashpw(dto.getPassword()));
        sysUserMapper.updateById(sysUser);
        redisUtil.delete(RedisConstants.CAPTCHA_CODE_KEY + dto.getEmail());
        return true;
    }

    @Override
    public String getWechatLoginCode() {
        //随机获取4位数字
        String code = "DL" + (int) ((ThreadLocalRandom.current().nextDouble() * 9 + 1) * 1000);
        redisUtil.set(RedisConstants.WX_LOGIN_USER_CODE + code, "NOT-LOGIN", RedisConstants.MINUTE_EXPIRE, TimeUnit.SECONDS);
        return code;
    }

    @Override
    public LoginUserInfo getWechatIsLogin(String loginCode) {
        Object value = redisUtil.get(RedisConstants.WX_LOGIN_USER + loginCode);

        if (value == null) {
            throw new ServiceException("登录失败");
        }

        LoginUserInfo loginUserInfo = JSONUtil.toBean(JSONUtil.parseObj(value), LoginUserInfo.class);

        StpUtil.login(loginUserInfo.getId());
        loginUserInfo.setToken(StpUtil.getTokenValue());

        return loginUserInfo;
    }

    @Override
    public String wechatLogin(WxMpXmlMessage message) {
        String code = message.getContent().toUpperCase();
        //先判断登录码是否已过期
        Object e = redisUtil.hasKey(RedisConstants.WX_LOGIN_USER_CODE + code);
        if (e == null) {
            return "验证码已过期";
        }
        LoginUserInfo loginUserInfo = wechatLogin(message.getFromUser());
        //修改redis缓存 以便监听是否已经授权成功
        redisUtil.set(RedisConstants.WX_LOGIN_USER + code, JSONUtil.toJsonStr(loginUserInfo), RedisConstants.MINUTE_EXPIRE, TimeUnit.SECONDS);
        return "网站登录成功！(若页面长时间未跳转请刷新验证码)";
    }

    @Override
    public String renderAuth(String source, String sourceType, String purpose) {
        // 后台场景使用 source + "_admin" 作为 config_key
        String configKey = "admin".equals(sourceType) ? source + "_admin" : source;
        AuthRequest authRequest = getAuthRequest(configKey);
        String state = AuthStateUtils.createState();
        if ("bind".equals(purpose)) {
            // 绑定场景，将当前用户ID编码到 state 中
            Integer userId = StpUtil.getLoginIdAsInt();
            state = "bind_" + userId + "_" + state;
        } else if ("admin".equals(sourceType)) {
            state = "admin_" + state;
        }
        return authRequest.authorize(state);
    }


    @Override
    public void authLogin(AuthCallback callback,String source, HttpServletResponse httpServletResponse) throws IOException {
        AuthRequest authRequest = getAuthRequest(source);
        AuthResponse<AuthUser> response = authRequest.login(callback);

        if (response.getData() == null) {
            log.info("用户取消了 {} 第三方登录",source);
            httpServletResponse.sendRedirect(Constants.LOGIN_URL);
            return;
        }
        String result = JSONObject.toJSONString(response.getData());
        log.info("第三方登录验证结果:{}", result);

        JSONObject jsonObject = JSON.parseObject(result);
        Object uuid = jsonObject.get("uuid");
        // 获取用户ip信息
        String ipAddress = IpUtil.getIp();
        String ipSource = IpUtil.getIp2region(ipAddress);
        // 判断是否已注册
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, uuid));
        if (ObjectUtils.isEmpty(user)) {
            // 保存账号信息
            user = SysUser.builder()
                    .username(uuid.toString())
                    .password(UUID.randomUUID().toString())
                    .loginType(source)
                    .lastLoginTime(LocalDateTime.now())
                    .ipLocation(ipAddress)
                    .ip(ipSource)
                    .status(Constants.YES)
                    .nickname(source + "-" +getRandomString(6))
                    .avatar(jsonObject.get("avatar").toString())
                    .build();
            userMapper.insert(user);
            //添加角色
            insertRole(user);
        }

        StpUtil.login(user.getId());
        httpServletResponse.sendRedirect(Constants.LOGIN_SUCCESS_URL + StpUtil.getTokenValue());
    }

    @Override
    public void adminAuthLogin(AuthCallback callback, String source, HttpServletResponse httpServletResponse) throws IOException {
        // 检测是否为绑定流程
        if (callback.getState() != null && callback.getState().startsWith("bind_")) {
            handleAdminBind(callback, source, httpServletResponse);
            return;
        }

        // 后台场景使用 source + "_admin" 作为 config_key 获取后台 OAuth 配置
        String configKey = source + "_admin";
        AuthRequest authRequest = getAuthRequest(configKey);
        AuthResponse<AuthUser> response = authRequest.login(callback);

        if (response.getData() == null) {
            log.info("管理员取消了 {} 第三方登录", source);
            httpServletResponse.sendRedirect(Constants.ADMIN_LOGIN_URL);
            return;
        }

        String result = JSONObject.toJSONString(response.getData());
        log.info("后台第三方登录验证结果:{}", result);

        JSONObject jsonObject = JSON.parseObject(result);
        String uuid = jsonObject.get("uuid").toString();

        // 查询绑定关系
        SysUserThirdParty binding = sysUserThirdPartyService.getByThirdParty(source, uuid);
        if (binding == null) {
            log.info("第三方账号未绑定后台用户，type={}, uuid={}", source, uuid);
            httpServletResponse.sendRedirect(Constants.ADMIN_LOGIN_URL + "?error=not_bound");
            return;
        }

        // 根据绑定的 user_id 查询用户
        SysUser user = userMapper.selectById(binding.getUserId());
        if (user == null) {
            log.warn("绑定的用户不存在，userId={}", binding.getUserId());
            httpServletResponse.sendRedirect(Constants.ADMIN_LOGIN_URL + "?error=user_not_found");
            return;
        }

        // 校验用户状态
        if (user.getStatus() != Constants.YES) {
            httpServletResponse.sendRedirect(Constants.ADMIN_LOGIN_URL + "?error=disabled");
            return;
        }

        StpUtil.login(user.getId());
        httpServletResponse.sendRedirect(Constants.ADMIN_LOGIN_SUCCESS_URL + StpUtil.getTokenValue());
    }

    /**
     * 处理后台绑定第三方账号流程
     * state 格式: bind_{userId}_{randomState}
     */
    private void handleAdminBind(AuthCallback callback, String source, HttpServletResponse httpServletResponse) throws IOException {
        String configKey = source + "_admin";
        AuthRequest authRequest = getAuthRequest(configKey);
        AuthResponse<AuthUser> response = authRequest.login(callback);

        if (response.getData() == null) {
            log.info("用户取消了 {} 绑定", source);
            httpServletResponse.sendRedirect(Constants.ADMIN_BIND_FAIL_URL + "用户取消授权");
            return;
        }

        // 从 state 中提取用户ID: bind_{userId}_{randomState}
        String state = callback.getState();
        Integer userId;
        try {
            String[] parts = state.split("_", 3);
            userId = Integer.parseInt(parts[1]);
        } catch (Exception e) {
            log.warn("绑定失败：无法从 state 解析用户ID, state={}", state);
            httpServletResponse.sendRedirect(Constants.ADMIN_BIND_FAIL_URL + "参数异常");
            return;
        }

        JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(response.getData()));
        String uuid = jsonObject.get("uuid").toString();
        String nickname = jsonObject.containsKey("nickname") ? jsonObject.get("nickname").toString() : source + "用户";
        String avatar = jsonObject.containsKey("avatar") ? jsonObject.get("avatar").toString() : null;

        try {
            sysUserThirdPartyService.bind(userId, source, uuid, nickname, avatar);
            log.info("用户 {} 绑定 {} 成功", userId, source);
            httpServletResponse.sendRedirect(Constants.ADMIN_BIND_SUCCESS_URL);
        } catch (ServiceException e) {
            log.warn("绑定失败: {}", e.getMessage());
            httpServletResponse.sendRedirect(Constants.ADMIN_BIND_FAIL_URL + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    @Override
    public LoginUserInfo appletLogin(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + wechatProperties.getAppletAppId()
                + "&secret=" + wechatProperties.getAppletSecret() + "&js_code=" + code + "&grant_type=authorization_code";
        String result = HttpUtil.get(url);
        com.alibaba.fastjson2.JSONObject jsonObject = JSON.parseObject(result);
        Object openid = jsonObject.get("openid");
        if (openid == null) {
            throw new ServiceException("登录失败");
        }

        // 查询用户
        SysUser user = userMapper.selectByUsername(openid.toString());

        if (user == null) {
            String ip = IpUtil.getIp();
            String avatar = avatarList[(int) (ThreadLocalRandom.current().nextDouble() * avatarList.length)];
            user = SysUser.builder()
                    .username(openid.toString())
                    .password(UUID.randomUUID().toString())
                    .loginType(LoginTypeEnum.APPLET.getType())
                    .lastLoginTime(LocalDateTime.now())
                    .ipLocation(IpUtil.getIp2region(ip))
                    .ip(ip)
                    .status(Constants.YES)
                    .nickname("applet-" + getRandomString(6))
                    .avatar(avatar)
                    .build();
            userMapper.insert(user);
            //添加用户角色信息
            this.insertRole(user);
        }else {
            if (user.getStatus() == Constants.NO) {
                throw new ServiceException("账号已被禁用，请联系管理员");
            }
        }

        LoginUserInfo loginUserInfo = BeanCopyUtil.copyObj(user, LoginUserInfo.class);

        StpUtil.login(loginUserInfo.getId());
        loginUserInfo.setToken(StpUtil.getTokenValue());

        return loginUserInfo;
    }

    @Override
    public Captcha getCaptcha() {
        Captcha captcha = new Captcha();
        //从redis中获取
        captcha.setPlace(1);
        CaptchaUtil.getCaptcha(captcha);
        return captcha;
    }

    private void validateEmailCode(EmailRegisterDto dto) {
        Object code = redisUtil.get(RedisConstants.CAPTCHA_CODE_KEY + dto.getEmail());
        if (code == null || !code.equals(dto.getCode())) {
            throw new ServiceException("验证码已过期或输入错误");
        }
    }

    private LoginUserInfo wechatLogin(String openId) {

        SysUser user = userMapper.selectByUsername(openId);
        if (ObjectUtils.isEmpty(user)) {
            String ip = IpUtil.getIp();
            String ipSource = IpUtil.getIp2region(ip);

            // 保存账号信息
            user = SysUser.builder()
                    .username(openId)
                    .password(BCrypt.hashpw(openId))
                    .nickname("WECHAT-" + getRandomString(6))
                    .avatar(avatarList[(int) (ThreadLocalRandom.current().nextDouble() * avatarList.length)])
                    .loginType(LoginTypeEnum.WECHAT.getType())
                    .lastLoginTime(LocalDateTime.now())
                    .ip(ip)
                    .ipLocation(ipSource)
                    .status(Constants.YES)
                    .build();
            userMapper.insert(user);

            //添加用户角色信息
            this.insertRole(user);
        }

        return BeanCopyUtil.copyObj(user, LoginUserInfo.class);
    }

    /**
     * 添加用户角色信息
     * @param user
     */
    private void insertRole(SysUser user) {
        SysRole sysRole = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, Constants.USER));
        sysRoleMapper.addRoleUser(user.getId(), Collections.singletonList(sysRole.getId()));
    }

    /**
     * 随机生成6位数的字符串
     */
    public static String getRandomString(int length) {
        String str = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    private @NotNull AuthRequest getAuthRequest(String configKey) {
        // 从数据库获取第三方登录配置
        SysThirdPartyConfig config = sysThirdPartyConfigService.getByConfigKey(configKey);
        if (config == null) {
            throw new ServiceException("未配置" + configKey + "第三方登录");
        }

        // 去掉 _admin 后缀，获取实际的第三方类型
        String source = configKey.replace("_admin", "");

        AuthRequest authRequest = null;
        switch (source) {
            case "gitee":
                authRequest = new AuthGiteeRequest(AuthConfig.builder()
                        .clientId(config.getAppId())
                        .clientSecret(config.getAppSecret())
                        .redirectUri(config.getRedirectUrl())
                        .build());
                break;
            case "qq":
                authRequest = new AuthQqRequest(AuthConfig.builder()
                        .clientId(config.getAppId())
                        .clientSecret(config.getAppSecret())
                        .redirectUri(config.getRedirectUrl())
                        .build());
                break;
            case "weibo":
                authRequest = new AuthWeiboRequest(AuthConfig.builder()
                        .clientId(config.getAppId())
                        .clientSecret(config.getAppSecret())
                        .redirectUri(config.getRedirectUrl())
                        .build());
                break;
            case "github":
                authRequest = new AuthGithubRequest(AuthConfig.builder()
                        .clientId(config.getAppId())
                        .clientSecret(config.getAppSecret())
                        .redirectUri(config.getRedirectUrl())
                        .build());
                break;
            default:
                break;
        }
        if (null == authRequest) {
            throw new AuthException("授权地址无效");
        }
        return authRequest;
    }

}
