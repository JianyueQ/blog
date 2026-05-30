package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户第三方账号绑定表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user_third_party")
@Schema(name = "用户第三方账号绑定表")
public class SysUserThirdParty implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "第三方类型：github/gitee/qq/wechat")
    private String thirdPartyType;

    @Schema(description = "第三方平台用户ID")
    private String thirdPartyId;

    @Schema(description = "第三方昵称")
    private String thirdPartyNickname;

    @Schema(description = "第三方头像")
    private String thirdPartyAvatar;

    @Schema(description = "绑定时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
