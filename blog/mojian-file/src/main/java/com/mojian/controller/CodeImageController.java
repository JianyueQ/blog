package com.mojian.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.common.Result;
import com.mojian.entity.CodeImage;
import com.mojian.exception.ServiceException;
import com.mojian.service.CodeImageCacheService;
import com.mojian.service.CodeImageService;
import com.mojian.utils.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@RestController
@Tag(name = "验证码图片管理")
@RequestMapping("/code/image")
@RequiredArgsConstructor
public class CodeImageController {

    private final CodeImageService codeImageService;

    private final FileStorageService fileStorageService;

    @GetMapping("/list")
    @SaCheckPermission("sys:codeImage:list")
    @Operation(summary = "获取验证码图片列表")
    public Result<IPage<CodeImage>> list(CodeImage codeImage) {
        return Result.success(codeImageService.selectPage(codeImage));
    }

    /**
     * 随机获取一张验证码图片（直接返回图片流，Content-Type: image/png）
     * <p>
     * 用于 CaptchaUtil 直接读取，避免 JSON 解析
     */
    @GetMapping("/random")
    @Operation(summary = "随机获取一张验证码图片")
    public void getRandomCodeImage(HttpServletResponse response) throws Exception {
        String imageUrl = codeImageService.getRandomCodeImageUrl();
        if (imageUrl == null) {
            response.setStatus(404);
            return;
        }

        // 通过URL读取图片内容
        URL url = new URL(imageUrl);
        BufferedImage bufferedImage;
        try (InputStream inputStream = url.openStream()) {
            bufferedImage = ImageIO.read(inputStream);
        }

        if (bufferedImage == null) {
            response.setStatus(404);
            return;
        }

        // 设置响应头
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        // 输出图片流
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", outputStream);
            response.getOutputStream().write(outputStream.toByteArray());
        }
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:codeImage:list")
    @Operation(summary = "获取验证码图片详情")
    public Result<CodeImage> getInfo(@PathVariable("id") String id) {
        return Result.success(codeImageService.getById(id));
    }

    @PostMapping("/upload")
    @SaCheckPermission("sys:codeImage:add")
    @Operation(summary = "上传验证码图片")
    public Result<String> upload(MultipartFile file, String source) {
        String path = DateUtil.parseDateToStr(DateUtil.YYYYMMDD, DateUtil.getNowDate()) + "/";
        if (source != null && !source.isEmpty()) {
            path = path + source + "/";
        }
        // 生成新文件名：UUID + 原扩展名
        String newFilename = IdUtil.fastSimpleUUID() + "." + FileUtil.extName(file.getOriginalFilename());
        // 上传文件
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath(path)
                .setSaveFilename(newFilename)
                .putAttr("source", source)
                .upload();

        if (fileInfo == null) {
            throw new ServiceException("上传文件失败");
        }

        // 保存到验证码图片库
        CodeImage codeImage = getCodeImage(file, source, fileInfo);
        codeImageService.insert(codeImage);

        return Result.success(fileInfo.getUrl());
    }

    @PostMapping("/add")
    @SaCheckPermission("sys:codeImage:add")
    @Operation(summary = "添加验证码图片")
    public Result<Object> add(@RequestBody CodeImage codeImage) {
        return Result.success(codeImageService.insert(codeImage));
    }

    @PutMapping("/update")
    @SaCheckPermission("sys:codeImage:update")
    @Operation(summary = "修改验证码图片")
    public Result<Object> edit(@RequestBody CodeImage codeImage) {
        return Result.success(codeImageService.update(codeImage));
    }

    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("sys:codeImage:delete")
    @Operation(summary = "删除验证码图片")
    public Result<Object> remove(@PathVariable List<String> ids) {
        return Result.success(codeImageService.deleteByIds(ids));
    }

    @NotNull
    private CodeImage getCodeImage(MultipartFile file, String source, FileInfo fileInfo) {
        CodeImage codeImage = new CodeImage();
        codeImage.setUrl(fileInfo.getUrl());
        codeImage.setSize(fileInfo.getSize());
        codeImage.setFilename(fileInfo.getFilename());
        codeImage.setOriginalFilename(file.getOriginalFilename());
        codeImage.setBasePath(fileInfo.getBasePath());
        codeImage.setPath(fileInfo.getPath());
        codeImage.setExt(fileInfo.getExt());
        codeImage.setContentType(fileInfo.getContentType());
        codeImage.setPlatform(fileInfo.getPlatform());
        codeImage.setThUrl(fileInfo.getThUrl());
        codeImage.setThFilename(fileInfo.getThFilename());
        codeImage.setThSize(fileInfo.getThSize());
        codeImage.setThContentType(fileInfo.getThContentType());
        codeImage.setSource(source);
        return codeImage;
    }
}
