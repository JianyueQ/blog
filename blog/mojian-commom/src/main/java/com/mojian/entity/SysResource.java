package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@TableName("sys_resource")
@Schema(name = "资源表对象")
public class SysResource implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "资源名")
    private String name;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "下载量")
    private Integer downloads;

    @Schema(description = "是否免费")
    private Integer isFree;

    @Schema(description = "付费方式")
    private Integer payType;

    @Schema(description = "网盘地址")
    private String panPath;

    @Schema(description = "网盘验证码")
    private String panCode;

    @Schema(description = "状态 0:未通过 1:待审核 2:通过")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
