package com.mojian.controller.message;

import com.mojian.annotation.AccessLimit;
import com.mojian.service.MessageService;
import com.mojian.common.Result;
import com.mojian.entity.SysMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Tag(name = "门户-留言管理")
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @AccessLimit
    @GetMapping("/list")
    @Operation(summary = "留言列表")
    public Result<List<SysMessage>> getMessageList() {
        return Result.success(messageService.getMessageList());
    }

    @PostMapping("/add")
    @Operation(summary = "发表留言")
    public Result<Boolean> add(@RequestBody SysMessage sysMessage) {
        return Result.success(messageService.add(sysMessage));
    }
}
