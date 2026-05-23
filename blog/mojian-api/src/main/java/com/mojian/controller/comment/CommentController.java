package com.mojian.controller.comment;


import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.service.CommentService;
import com.mojian.vo.comment.CommentListVo;
import com.mojian.common.Result;
import com.mojian.entity.SysComment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "门户-评论管理")
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/list")
    @Operation(summary = "获取文章评论列表")
    public Result<IPage<CommentListVo>> getComments(Integer articleId,String sortType) {
        return Result.success(commentService.getComments(articleId,sortType));
    }

    @SaCheckLogin
    @PostMapping("/add")
    @Operation(summary = "获取文章评论列表")
    public Result<Void> add(@RequestBody SysComment sysComment) {
        commentService.add(sysComment);
        return Result.success();
    }
}
