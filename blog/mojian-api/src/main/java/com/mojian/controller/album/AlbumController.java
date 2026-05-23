package com.mojian.controller.album;

import com.mojian.annotation.AccessLimit;
import com.mojian.common.Result;
import com.mojian.entity.SysAlbum;
import com.mojian.entity.SysPhoto;
import com.mojian.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: quequnlong
 * @date: 2025/2/7
 * @description:
 */
@RestController
@Tag(name = "门户-相册管理")
@RequestMapping("/api/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/list")
    @Operation(summary = "获取相册列表")
    public Result<List<SysAlbum>> getAlbumList() {
        return Result.success(albumService.getAlbumList());
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "获取相册详情")
    public Result<SysAlbum> detail(@PathVariable Long id) {
        return Result.success(albumService.detail(id));
    }

    @GetMapping("/photos/{albumId}")
    @Operation(summary = "获取照片列表")
    public Result<List<SysPhoto>> getPhotos(@PathVariable Long albumId) {
        return Result.success(albumService.getPhotos(albumId));
    }

    @AccessLimit(count = 5, time = 30)
    @GetMapping("/verify/{id}")
    @Operation(summary = "验证相册的密码")
    public Result<Boolean> verify(@PathVariable Long id,String password) {
        return albumService.verify(id,password) ? Result.success(true) : Result.error("密码错误");
    }
}
