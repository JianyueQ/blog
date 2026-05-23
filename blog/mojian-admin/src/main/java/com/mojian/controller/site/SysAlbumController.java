package com.mojian.controller.site;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysAlbum;
import com.mojian.service.SysAlbumService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 相册 控制器
 */
@RestController
@Tag(name = "相册管理")
@RequestMapping("/sys/album")
@RequiredArgsConstructor
public class SysAlbumController {

    private final SysAlbumService sysAlbumService;

    @GetMapping("/list")
    @Operation(summary = "获取相册列表")
    public Result<IPage<SysAlbum>> selectPage(SysAlbum sysAlbum) {
        return Result.success(sysAlbumService.selectPage(sysAlbum));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有相册列表")
    public Result<List<SysAlbum>> selectList(SysAlbum sysAlbum) {
        return Result.success(sysAlbumService.selectList(sysAlbum));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取相册详情")
    public Result<SysAlbum> getInfo(@PathVariable("id") Long id) {
        return Result.success(sysAlbumService.getById(id));
    }

    @PostMapping("/add")
    @SaCheckPermission("sys:album:add")
    @Operation(summary = "添加相册")
    public Result<Object> add(@RequestBody SysAlbum sysAlbum) {
        return Result.success(sysAlbumService.insert(sysAlbum));
    }

    @PutMapping("/update")
    @SaCheckPermission("sys:album:update")
    @Operation(summary = "修改相册")
    public Result<Object> edit(@RequestBody SysAlbum sysAlbum) {
        return Result.success(sysAlbumService.update(sysAlbum));
    }

    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("sys:album:delete")
    @Operation(summary = "删除相册")
    public Result<Object> remove(@PathVariable List<Long> ids) {
        return Result.success(sysAlbumService.deleteByIds(ids));
    }
}
