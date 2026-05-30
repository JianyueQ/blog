package com.mojian.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.mojian.common.Result;
import com.mojian.dto.Captcha;
import com.mojian.dto.EmailRegisterDto;
import com.mojian.dto.LoginDTO;
import com.mojian.entity.SysThirdPartyConfig;
import com.mojian.entity.SysUserThirdParty;
import com.mojian.service.AuthService;
import com.mojian.service.SysThirdPartyConfigService;
import com.mojian.service.SysUserThirdPartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.model.AuthCallback;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.mojian.dto.user.*;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@Tag(name = "认证管理")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final SysThirdPartyConfigService sysThirdPartyConfigService;

    private final SysUserThirdPartyService sysUserThirdPartyService;

    @RequestMapping({"/api/auth/render/{source}", "/auth/render/{source}"})
    @Operation(summary = "获取第三方授权地址")
    public Result<String> renderAuth(HttpServletResponse response, 
                                     @PathVariable String source,
                                     @RequestParam(required = false) String sourceType,
                                     @RequestParam(required = false) String purpose) {
        return Result.success(authService.renderAuth(source, sourceType, purpose));
    }

    @RequestMapping({"/api/auth/callback/{source}", "/auth/callback/{source}"})
    public void login(AuthCallback callback, @PathVariable String source, HttpServletResponse httpServletResponse) throws IOException {
        authService.authLogin(callback, source, httpServletResponse);
    }

    @SaIgnore
    @RequestMapping({"/api/auth/callback/admin/{source}", "/auth/callback/admin/{source}"})
    @Operation(summary = "后台第三方授权回调")
    public void adminLogin(AuthCallback callback, @PathVariable String source,
                           HttpServletResponse httpServletResponse) throws IOException {
        authService.adminAuthLogin(callback, source, httpServletResponse);
    }


    @Operation(summary = "用户登录")
    @PostMapping("/auth/login")
    public Result<LoginUserInfo> login(@Validated @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @SaIgnore
    @Operation(summary = "获取滑块验证码")
    @GetMapping("/auth/getCaptcha")
    public Result<Captcha> getCaptcha() {
        return Result.success(authService.getCaptcha());
    }

    @Operation(summary = "用户登出")
    @PostMapping("/auth/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success(null);
    }

    @Operation(summary = "发送注册邮箱验证码")
    @GetMapping("/api/sendEmailCode")
    public Result<Boolean> sendEmailCode(String email) throws MessagingException {
        return Result.success(authService.sendEmailCode(email));
    }

    @Operation(summary = "邮箱账号注册")
    @PostMapping("/api/email/register")
    public Result<Boolean> register(@RequestBody EmailRegisterDto dto){
        return Result.success(authService.register(dto));
    }

    @Operation(summary = "根据邮箱修改密码")
    @PostMapping("/api/email/forgot")
    public Result<Boolean> forgot(@RequestBody EmailRegisterDto dto){
        return Result.success(authService.forgot(dto));
    }

    @Operation(summary = "获取微信扫码登录验证码")
    @GetMapping("/api/wechat/getCode")
    public Result<String> getWechatLoginCode(){
        return Result.success(authService.getWechatLoginCode());
    }

    @Operation(summary = "获取微信扫码登录验证码")
    @GetMapping("/api/wechat/isLogin/{loginCode}")
    public Result<LoginUserInfo> getWechatIsLogin(@PathVariable String loginCode){
        return Result.success(authService.getWechatIsLogin(loginCode));
    }

    @Operation(summary = "微信小程序登录")
    @GetMapping("/api/wechat/appletLogin/{code}")
    public Result<LoginUserInfo> appletLogin(@PathVariable String code){
        return Result.success(authService.appletLogin(code));
    }

    @GetMapping("/auth/info")
    public Result<LoginUserInfo> getUserInfo(@RequestParam(defaultValue = "admin") String source) {
        return Result.success(authService.getLoginUserInfo(source));
    }

    @SaIgnore
    @Operation(summary = "获取已启用的前台第三方登录配置列表")
    @GetMapping({"/api/thirdPartyConfig/front/enabledList", "/thirdPartyConfig/front/enabledList"})
    public Result<List<SysThirdPartyConfig>> getFrontEnabledThirdPartyConfig() {
        return Result.success(sysThirdPartyConfigService.getFrontEnabledList());
    }

    @SaIgnore
    @Operation(summary = "获取已启用的后台第三方登录配置列表")
    @GetMapping({"/api/thirdPartyConfig/admin/enabledList", "/thirdPartyConfig/admin/enabledList"})
    public Result<List<SysThirdPartyConfig>> getAdminEnabledThirdPartyConfig() {
        return Result.success(sysThirdPartyConfigService.getAdminEnabledList());
    }

    @Operation(summary = "获取当前用户绑定的第三方账号列表")
    @GetMapping("/api/userThirdParty/list")
    public Result<List<SysUserThirdParty>> listUserThirdParty() {
        Integer userId = StpUtil.getLoginIdAsInt();
        return Result.success(sysUserThirdPartyService.listByUserId(userId));
    }

    @Operation(summary = "解绑第三方账号")
    @DeleteMapping("/api/userThirdParty/unbind/{type}")
    public Result<Boolean> unbindThirdParty(@PathVariable String type) {
        Integer userId = StpUtil.getLoginIdAsInt();
        return Result.success(sysUserThirdPartyService.unbind(userId, type));
    }

}
