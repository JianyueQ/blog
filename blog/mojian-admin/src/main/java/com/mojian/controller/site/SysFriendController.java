package com.mojian.controller.site;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysFriend;
import com.mojian.service.SysFriendService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 友情链接 控制器
 */
@RestController
@Tag(name = "友情链接管理")
@RequestMapping("/sys/friend")
@RequiredArgsConstructor
public class SysFriendController {

    private final SysFriendService sysFriendService;

    @GetMapping("/list")
    @Operation(summary = "友情链接列表")
    public Result<IPage<SysFriend>> list(SysFriend sysFriend) {
        return Result.success(sysFriendService.selectPage(sysFriend));
    }

    @PostMapping("add")
    @Operation(summary = "新增友情链接")
    @SaCheckPermission("sys:friend:add")
    public Result<Object> add(@RequestBody SysFriend sysFriend) {
        return Result.success(sysFriendService.save(sysFriend));
    }

    @PutMapping("update")
    @Operation(summary = "修改友情链接")
    @SaCheckPermission("sys:friend:update")
    public Result<Object> update(@RequestBody SysFriend sysFriend) {
        return Result.success(sysFriendService.update(sysFriend));
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除友情链接")
    @SaCheckPermission("sys:friend:delete")
    public Result<Object> delete(@PathVariable List<Integer> ids) {
        return Result.success(sysFriendService.removeBatchByIds(ids));
    }
}
