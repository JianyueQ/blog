package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 文章
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_article")
@Schema(name = "文章对象 gen_table")
public class SysArticle implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "分类id")
    private Integer categoryId;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章封面地址")
    private String cover;

    @Schema(description = "文章简介")
    private String summary;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "文章内容md格式")
    private String contentMd;

    @Schema(description = "阅读方式 0无需验证 1：评论阅读 2：点赞阅读 3：扫码阅读")
    private Integer readType;

    @Schema(description = "是否置顶 0否 1是")
    private Integer isStick;

    @Schema(description = "状态 0：下架 1：发布")
    private Integer status;

    @Schema(description = "是否原创  0：转载 1:原创")
    private Integer isOriginal;

    @Schema(description = "是否首页轮播")
    private Integer isCarousel;

    @Schema(description = "是否推荐")
    private Integer isRecommend;

    @Schema(description = "转载地址")
    private String originalUrl;

    @Schema(description = "文章阅读量")
    private Integer quantity;

    @Schema(description = "关键词")
    private String keywords;


    @Schema(description = "Ai生成的简短描述")
    private String aiDescribe;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime updateTime;
}
