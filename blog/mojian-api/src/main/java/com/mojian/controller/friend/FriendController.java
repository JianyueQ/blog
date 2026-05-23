package com.mojian.controller.friend;

import com.mojian.service.FriendService;
import com.mojian.common.Result;
import com.mojian.entity.SysFriend;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "门户-友情链接管理")
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/list")
    @Operation(summary = "友情链接列表")
    public Result<List<SysFriend>> getFriendList() {
        return Result.success(friendService.getFriendList());
    }

    @PostMapping("/apply")
    @Operation(summary = "申请友链")
    public Result<Boolean> apply(@RequestBody SysFriend sysFriend) {
        return Result.success(friendService.apply(sysFriend));
    }
}
