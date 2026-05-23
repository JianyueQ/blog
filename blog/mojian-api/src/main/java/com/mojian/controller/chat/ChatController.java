package com.mojian.controller.chat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.common.Result;
import com.mojian.vo.chat.ChatSendMsgVo;
import com.mojian.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "门户-聊天管理")
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/list")
    @Operation(summary = "获取聊天历史纪录")
    public Result<IPage<ChatSendMsgVo>> getChatMsgList() {
        return Result.success(chatService.getChatMsgList());
    }

    @PostMapping("/sendMsg")
    @Operation(summary = "发送消息")
    public Result<Void> sendMsg(@RequestBody ChatSendMsgVo chatSendMsgVo) {
        chatService.sendMsg(chatSendMsgVo);
        return Result.success();
    }

    @PostMapping("/recallMsg")
    @Operation(summary = "撤回消息")
    public Result<Void> recallMsg(@RequestBody ChatSendMsgVo chatSendMsgVo) {
        chatService.recallMsg(chatSendMsgVo);
        return Result.success();
    }

}
