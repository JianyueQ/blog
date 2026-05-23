package com.mojian.controller.message;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mojian.common.Result;
import com.mojian.entity.SysMessage;
import com.mojian.service.SysMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: quequnlong
 * @date: 2025/1/2
 * @description:
 */
@RestController
@Tag(name = "留言管理")
@RequestMapping("/sys/message")
@RequiredArgsConstructor
public class SysMessageController {

    private final SysMessageService sysMessageService;

    @GetMapping("/list")
    @Operation(summary = "获取留言列表")
    public Result<Page<SysMessage>> list() {
        return Result.success(sysMessageService.selectList());
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除留言")
    @SaCheckPermission("sys:message:delete")
    public Result<Void> delete(@PathVariable List<Integer> ids) {
        sysMessageService.removeBatchByIds(ids);
        return Result.success();
    }
}
