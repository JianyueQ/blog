package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("chat_msg")
@Schema(name = "聊天消息对象")
public class ChatMsg implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "聊天ID")
    private Long chatId;

    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "消息类型，可选值为'text'、'image'，默认'text'")
    private String type;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "ip")
    private String ip;

    @Schema(description = "ip归属地")
    private String location;

    @Schema(description = "是否撤回，默认false")
    private Boolean isRecalled;

    @Schema(description = "消息是文件时，存的文件名")
    private String fileName;

    @Schema(description = "文件大小")
    private String fileSize;

    @Schema(description = "语音时长")
    private Integer duration;

    @Schema(description = "发送时间")
    private LocalDateTime createTime;


    @Schema(description = "回复的消息id")
    private String replyId;

    @Schema(description = "回复的用户id")
    private String replyUserId;

    @Schema(description = "回复的消息内容")
    private String replyContent;

}
