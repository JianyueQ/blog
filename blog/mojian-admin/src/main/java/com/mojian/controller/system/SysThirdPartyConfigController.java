package com.mojian.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysThirdPartyConfig;
import com.mojian.service.SysThirdPartyConfigService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 第三方登录配置 控制器
 * 仅支持查询和修改，不允许新增和删除（固定支持 Gitee / GitHub / QQ 三种登录方式）
 */
@RestController
@Tag(name = "第三方登录配置管理")
@RequestMapping("/sys/thirdPartyConfig")
@RequiredArgsConstructor
public class SysThirdPartyConfigController {

    private final SysThirdPartyConfigService sysThirdPartyConfigService;

    @GetMapping("/list")
    @SaCheckPermission("sys:thirdPartyConfig:list")
    @Operation(summary = "获取第三方登录配置列表")
    public Result<IPage<SysThirdPartyConfig>> list(SysThirdPartyConfig query) {
        return Result.success(sysThirdPartyConfigService.selectPage(query));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:thirdPartyConfig:list")
    @Operation(summary = "获取第三方登录配置详情")
    public Result<SysThirdPartyConfig> getInfo(@PathVariable("id") Long id) {
        return Result.success(sysThirdPartyConfigService.getById(id));
    }

    @PutMapping("/update")
    @SaCheckPermission("sys:thirdPartyConfig:update")
    @Operation(summary = "修改第三方登录配置")
    public Result<Object> edit(@RequestBody SysThirdPartyConfig config) {
        return Result.success(sysThirdPartyConfigService.update(config));
    }
}
