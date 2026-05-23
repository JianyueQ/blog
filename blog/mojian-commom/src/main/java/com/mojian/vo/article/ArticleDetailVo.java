package com.mojian.vo.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import com.mojian.vo.tag.TagListVo;
import com.mojian.entity.SysCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(name = "文章详情视图对象")
public class ArticleDetailVo {

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "用户id")
    private Integer userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String contentMd;
    private String content;

    @Schema(description = "封面")
    private String cover;

    @Schema(description = "阅读量")
    private Integer quantity;

    @Schema(description = "阅读方式")
    private Integer readType;

    @Schema(description = "评论数量")
    private Integer commentNum;

    @Schema(description = "点赞数量")
    private Integer likeNum;

    @Schema(description = "是否原创")
    private Integer isOriginal;

    @Schema(description = "转载地址")
    private String originalUrl;

    @Schema(description = "是否点赞")
    private Boolean isLike;

    @Schema(description = "分类")
    private SysCategory category;

    @Schema(description = "Ai生成的简短描述")
    private String aiDescribe;

    @Schema(description = "标签列表")
    private List<TagListVo> tags;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD)
    private LocalDateTime createTime;
}
