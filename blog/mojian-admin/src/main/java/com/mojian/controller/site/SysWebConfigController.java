package com.mojian.controller.site;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mojian.common.Result;
import com.mojian.entity.SysWebConfig;
import com.mojian.service.SysWebConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "网站配置管理")
@RequestMapping("/sys/web")
@RequiredArgsConstructor
public class SysWebConfigController {

    private final SysWebConfigService sysWebConfigService;

    @GetMapping("/config")
    @Operation(summary = "获取网站配置")
    public Result<SysWebConfig> getWebConfig() {
        return Result.success(sysWebConfigService.getOne(new LambdaQueryWrapper<SysWebConfig>().last("limit 1")));
    }

    @PutMapping("/update")
    @Operation(summary = "修改网站配置")
    @SaCheckPermission("sys:web:update")
    public Result<Void> update(@RequestBody SysWebConfig sysWebConfig) {
        sysWebConfigService.update(sysWebConfig);
        return Result.success();
    }
}
