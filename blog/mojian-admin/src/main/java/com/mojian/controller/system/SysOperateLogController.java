package com.mojian.controller.system;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysOperateLog;
import com.mojian.service.SysOperateLogService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;


@RestController
@Tag(name = "操作日志管理")
@RequestMapping("/sys/operateLog")
@RequiredArgsConstructor
public class SysOperateLogController {

    private final SysOperateLogService sysOperateLogService;

    @GetMapping
    @Operation(summary = "获取操作日志列表")
    public Result<IPage<SysOperateLog>> list(SysOperateLog sysOperateLog) {
        return Result.success(sysOperateLogService.listSysOperateLog(sysOperateLog));
    }

    @DeleteMapping("delete/{ids}")
    @Operation(summary = "批量删除操作日志")
    @SaCheckPermission("sys:operateLog:delete")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        sysOperateLogService.removeBatchByIds(ids);
        return Result.success();
    }
}
