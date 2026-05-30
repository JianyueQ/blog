package com.mojian.vo.frontMenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "前台菜单返回VO")
public class FrontMenuVO {

    @Schema(description = "菜单ID")
    private Integer id;

    @Schema(description = "菜单名称")
    private String title;

    @Schema(description = "路由路径或外链URL")
    private String path;

    @Schema(description = "图标类名")
    private String icon;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "是否隐藏：0显示 1隐藏")
    private Integer hidden;

    @Schema(description = "是否外链：0否 1是")
    private Integer isExternal;

    @Schema(description = "CSS颜色类名")
    private String colorClass;

    @Schema(description = "子菜单列表")
    private List<FrontMenuVO> children;
}
