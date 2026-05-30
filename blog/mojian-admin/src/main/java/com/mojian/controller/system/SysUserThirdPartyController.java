package com.mojian.controller.system;

import cn.dev33.satoken.stp.StpUtil;
import com.mojian.common.Result;
import com.mojian.entity.SysUserThirdParty;
import com.mojian.service.SysUserThirdPartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户第三方账号绑定管理
 */
@RestController
@Tag(name = "用户第三方账号绑定管理")
@RequestMapping("/sys/userThirdParty")
@RequiredArgsConstructor
public class SysUserThirdPartyController {

    private final SysUserThirdPartyService sysUserThirdPartyService;

    @GetMapping("/list")
    @Operation(summary = "获取当前用户绑定的第三方账号列表")
    public Result<List<SysUserThirdParty>> list() {
        Integer userId = StpUtil.getLoginIdAsInt();
        return Result.success(sysUserThirdPartyService.listByUserId(userId));
    }

    @DeleteMapping("/unbind/{type}")
    @Operation(summary = "解绑第三方账号")
    public Result<Boolean> unbind(@PathVariable String type) {
        Integer userId = StpUtil.getLoginIdAsInt();
        return Result.success(sysUserThirdPartyService.unbind(userId, type));
    }
}
