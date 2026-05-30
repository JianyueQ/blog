package com.mojian.controller.home;

import cn.dev33.satoken.annotation.SaIgnore;
import com.mojian.common.Result;
import com.mojian.service.SysFrontMenuService;
import com.mojian.vo.frontMenu.FrontMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "门户-前台菜单")
@RequestMapping("/api/frontMenu")
@RequiredArgsConstructor
public class FrontMenuController {

    private final SysFrontMenuService sysFrontMenuService;

    @GetMapping("/navList")
    @SaIgnore
    @Operation(summary = "获取前台导航菜单列表")
    public Result<List<FrontMenuVO>> getNavList() {
        return Result.success(sysFrontMenuService.getNavList());
    }
}
