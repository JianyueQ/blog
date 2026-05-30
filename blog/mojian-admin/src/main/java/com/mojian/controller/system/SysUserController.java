package com.mojian.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.annotation.OperationLogger;
import com.mojian.dto.user.SysUserAddAndUpdateDto;
import com.mojian.common.Result;
import com.mojian.dto.user.UpdatePwdDTO;
import com.mojian.entity.SysUser;
import com.mojian.service.SysUserService;
import com.mojian.vo.user.SysUserVo;
import com.mojian.vo.user.SysUserProfileVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "用户管理")
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping
    @Operation(summary = "获取用户列表")
    public Result<IPage<SysUserVo>> listUsers(SysUser sysUser) {
        return Result.success(sysUserService.listUsers(sysUser));
    }

    @PostMapping
    @OperationLogger("新增用户")
    @Operation(summary = "新增用户")
    @SaCheckPermission("sys:user:add")
    public Result<Void> addUser(@RequestBody SysUserAddAndUpdateDto sysUserAddDto) {
        sysUserService.add(sysUserAddDto);
        return Result.success();
    }

    @PutMapping
    @OperationLogger("修改用户")
    @Operation(summary = "修改用户")
    @SaCheckPermission("sys:user:update")
    public Result<Void> update(@RequestBody SysUserAddAndUpdateDto user) {
        sysUserService.update(user);
        return Result.success();
    }

    @DeleteMapping("/delete/{ids}")
    @OperationLogger("批量删除用户")
    @Operation(summary = "批量删除用户")
    @SaCheckPermission("sys:user:delete")
    public Result<Void> delete(@PathVariable List<Integer> ids) {
        sysUserService.delete(ids);
        return Result.success();
    }

    @PutMapping("/updatePwd")
    @Operation(summary = "修改密码")
    @SaCheckPermission("sys:profile:password")
    public Result<Void> updatePwd(@RequestBody UpdatePwdDTO updatePwdDTO) {
        sysUserService.updatePwd(updatePwdDTO);
        return Result.success();
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public Result<SysUserProfileVo> profile() {
        return Result.success(sysUserService.profile());
    }

    @PutMapping("/updProfile")
    @OperationLogger("修改个人信息")
    @Operation(summary = "修改个人信息")
    @SaCheckPermission("sys:profile:edit")
    public Result<SysUserProfileVo> updateProfile(@RequestBody SysUser user) {
        sysUserService.updateProfile(user);
        return Result.success();
    }

    @GetMapping("/verifyPassword/{password}")
    @Operation(summary = "锁屏界面验证密码")
    public Result<Boolean> verifyPassword(@PathVariable String password) {
        return Result.success(sysUserService.verifyPassword(password));
    }

    @PutMapping("/reset")
    @OperationLogger("重置密码")
    @Operation(summary = "重置密码")
    @SaCheckPermission("sys:user:reset")
    public Result<Boolean> resetPassword(@RequestBody SysUser user) {
        return Result.success(sysUserService.resetPassword(user));
    }
}
