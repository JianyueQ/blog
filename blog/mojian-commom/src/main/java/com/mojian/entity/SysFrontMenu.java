package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_front_menu")
@Schema(name = "前台菜单")
public class SysFrontMenu implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "父菜单ID，一级菜单为0")
    private Integer parentId;

    @Schema(description = "菜单名称")
    private String title;

    @Schema(description = "路由路径或外链URL")
    private String path;

    @Schema(description = "图标类名（亮色模式）")
    private String icon;

    @Schema(description = "图标类名（暗色模式）")
    private String iconDark;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "是否隐藏：0显示 1隐藏")
    private Integer hidden;

    @Schema(description = "是否外链：0否 1是")
    private Integer isExternal;

    @Schema(description = "CSS颜色类名")
    private String colorClass;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS, timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS, timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<SysFrontMenu> children;
}
