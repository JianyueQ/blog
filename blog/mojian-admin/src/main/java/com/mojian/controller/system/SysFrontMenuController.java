package com.mojian.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mojian.annotation.OperationLogger;
import com.mojian.common.Result;
import com.mojian.entity.SysFrontMenu;
import com.mojian.service.SysFrontMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "前台菜单管理")
@RequestMapping("/sys/frontMenu")
@RequiredArgsConstructor
public class SysFrontMenuController {

    private final SysFrontMenuService sysFrontMenuService;

    @GetMapping("/tree")
    @Operation(summary = "获取前台菜单树列表")
    public Result<List<SysFrontMenu>> getMenuTree() {
        return Result.success(sysFrontMenuService.getMenuTree());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取单个前台菜单")
    public Result<SysFrontMenu> getMenuById(@PathVariable Integer id) {
        return Result.success(sysFrontMenuService.getMenuById(id));
    }

    @PostMapping
    @Operation(summary = "添加前台菜单")
    @OperationLogger(value = "添加前台菜单")
    @SaCheckPermission("sys:frontMenu:add")
    public Result<Void> addMenu(@RequestBody SysFrontMenu menu) {
        sysFrontMenuService.addMenu(menu);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改前台菜单")
    @OperationLogger(value = "修改前台菜单")
    @SaCheckPermission("sys:frontMenu:update")
    public Result<Void> updateMenu(@RequestBody SysFrontMenu menu) {
        sysFrontMenuService.updateMenu(menu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除前台菜单")
    @OperationLogger(value = "删除前台菜单")
    @SaCheckPermission("sys:frontMenu:delete")
    public Result<Void> deleteMenu(@PathVariable Integer id) {
        sysFrontMenuService.deleteMenu(id);
        return Result.success();
    }
}
