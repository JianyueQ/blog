package com.mojian.vo.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "聊天消息")
public class ChatSendMsgVo {

    @Schema(description = "消息id")
    private Long id;

    @Schema(description = "消息类型，可选值为'text'、'image'，默认'text'")
    private String type;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "是否自己发送")
    private Long userId;

    @Schema(description = "发送人名称")
    private String name;

    @Schema(description = "发送人头像")
    private String avatar;

    @Schema(description = "性别")
    private Integer sex;

    @Schema(description = "归属地")
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
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime time;

    @Schema(description = "回复的消息id")
    private String replyId;

    @Schema(description = "回复的用户id")
    private String replyUserId;

    @Schema(description = "回复的消息内容")
    private String replyContent;

    @Schema(description = "回复用户昵称")
    private String replyUserName;


}
