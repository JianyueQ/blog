package com.mojian.controller.site;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysNotice;
import com.mojian.service.SysNoticeService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 公告 控制器
 */
@RestController
@Tag(name = "公告管理")
@RequestMapping("/sys/notice")
@RequiredArgsConstructor
public class SysNoticeController {

    private final SysNoticeService sysNoticeService;

    @GetMapping("/list")
    @Operation(summary = "获取公告列表")
    public Result<IPage<SysNotice>> list(SysNotice sysNotice) {
        return Result.success(sysNoticeService.selectPage(sysNotice));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取公告详情")
    public Result<SysNotice> getInfo(@PathVariable("id") Long id) {
        return Result.success(sysNoticeService.getById(id));
    }

    @PostMapping("/add")
    @SaCheckPermission("sys:notice:add")
    @Operation(summary = "添加公告")
    public Result<Object> add(@RequestBody SysNotice sysNotice) {
        return Result.success(sysNoticeService.insert(sysNotice));
    }

    @PutMapping("/update")
    @SaCheckPermission("sys:notice:update")
    @Operation(summary = "修改公告")
    public Result<Object> edit(@RequestBody SysNotice sysNotice) {
        return Result.success(sysNoticeService.update(sysNotice));
    }

    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("sys:notice:delete")
    @Operation(summary = "删除公告")
    public Result<Object> remove(@PathVariable List<Long> ids) {
        return Result.success(sysNoticeService.deleteByIds(ids));
    }
}
