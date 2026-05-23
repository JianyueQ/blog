package com.mojian.controller.article;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysMoment;
import com.mojian.service.SysMomentService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 说说 控制器
 */
@RestController
@Tag(name = "说说管理")
@RequestMapping("/sys/moment")
@RequiredArgsConstructor
public class SysMomentController {

    private final SysMomentService sysMomentService;

    @GetMapping("/list")
    @Operation(summary = "获取说说列表")
    public Result<IPage<SysMoment>> list(SysMoment sysMoment) {
        return Result.success(sysMomentService.selectPage(sysMoment));
    }

    @PostMapping("/add")
    @SaCheckPermission("sys:moment:add")
    @Operation(summary = "添加说说")
    public Result<Object> add(@RequestBody SysMoment sysMoment) {
        return Result.success(sysMomentService.add(sysMoment));
    }

    @PutMapping("/update")
    @SaCheckPermission("sys:moment:update")
    @Operation(summary = "修改说说")
    public Result<Object> edit(@RequestBody SysMoment sysMoment) {
        return Result.success(sysMomentService.updateById(sysMoment));
    }

    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("sys:moment:delete")
    @Operation(summary = "删除说说")
    public Result<Object> remove(@PathVariable List<Long> ids) {
        return Result.success(sysMomentService.removeByIds(ids));
    }
}
