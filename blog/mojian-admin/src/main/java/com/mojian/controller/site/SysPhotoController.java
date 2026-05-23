package com.mojian.controller.site;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysPhoto;
import com.mojian.service.SysPhotoService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 照片 控制器
 */
@RestController
@Tag(name = "照片管理")
@RequestMapping("/sys/photo")
@RequiredArgsConstructor
public class SysPhotoController {

    private final SysPhotoService sysPhotoService;

    @GetMapping("/list")
    @Operation(summary = "获取照片列表")
    public Result<IPage<SysPhoto>> list(SysPhoto sysPhoto) {
        return Result.success(sysPhotoService.selectPage(sysPhoto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取照片详情")
    public Result<SysPhoto> getInfo(@PathVariable("id") Long id) {
        return Result.success(sysPhotoService.getById(id));
    }

    @PostMapping("/add")
    @SaCheckPermission("sys:photo:add")
    @Operation(summary = "添加照片")
    public Result<Object> add(@RequestBody SysPhoto sysPhoto) {
        return Result.success(sysPhotoService.insert(sysPhoto));
    }

    @PutMapping("/update")
    @SaCheckPermission("sys:photo:update")
    @Operation(summary = "修改照片")
    public Result<Object> edit(@RequestBody SysPhoto sysPhoto) {
        return Result.success(sysPhotoService.update(sysPhoto));
    }

    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("sys:photo:delete")
    @Operation(summary = "删除照片")
    public Result<Object> remove(@PathVariable List<Long> ids) {
        return Result.success(sysPhotoService.deleteByIds(ids));
    }

    @PutMapping("/move/{ids}")
    @SaCheckPermission("sys:photo:move")
    @Operation(summary = "移动照片")
    public Result<Object> move(@PathVariable List<Long> ids, @RequestParam Long albumId) {
        return Result.success(sysPhotoService.move(ids,albumId));
    }
}
