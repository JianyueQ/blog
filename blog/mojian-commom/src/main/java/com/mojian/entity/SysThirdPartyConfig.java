package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 第三方登录配置表
 */
@Data
@TableName("sys_third_party_config")
@Schema(name = "第三方登录配置表对象")
public class SysThirdPartyConfig implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "配置键名：github/gitee/qq")
    private String configKey;

    @Schema(description = "配置来源：front前台/admin后台")
    private String configSource;

    @Schema(description = "配置名称：Github/Gitee/QQ")
    private String configName;

    @Schema(description = "应用ID")
    private String appId;

    @Schema(description = "应用密钥")
    private String appSecret;

    @Schema(description = "回调地址")
    private String redirectUrl;

    @Schema(description = "图标URL")
    private String icon;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
