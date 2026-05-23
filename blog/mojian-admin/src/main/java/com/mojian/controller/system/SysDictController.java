package com.mojian.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.annotation.OperationLogger;
import com.mojian.common.Result;
import com.mojian.entity.SysDict;
import com.mojian.service.SysDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "字典管理")
@RequestMapping("/sys/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictService sysDictService;

    @GetMapping
    @Operation(summary = "获取字典列表")
    public Result<IPage<SysDict>> getDictList(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) Integer status) {
        return Result.success(sysDictService.getDictPageList(name,status));
    }

    @PostMapping("/add")
    @Operation(summary = "添加字典")
    @OperationLogger(value = "添加字典")
    @SaCheckPermission("sys:dict:add")
    public Result<Void> addDict(@RequestBody SysDict dict) {
        sysDictService.addDict(dict);
        return Result.success();
    }

    @PutMapping("/update")
    @Operation(summary = "修改字典")
    @OperationLogger(value = "修改字典")
    @SaCheckPermission("sys:dict:update")
    public Result<Void> updateDict(@RequestBody SysDict dict) {
        sysDictService.updateDict(dict);
        return Result.success();
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除字典")
    @OperationLogger(value = "删除字典")
    @SaCheckPermission("sys:dict:delete")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        sysDictService.removeBatchByIds(ids);
        return Result.success();
    }
}
