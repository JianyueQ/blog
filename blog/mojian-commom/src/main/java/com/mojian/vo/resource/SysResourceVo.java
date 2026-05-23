package com.mojian.vo.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: quequnlong
 * @date: 2025/3/12
 * @description:
 */
@Data
public class SysResourceVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

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

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
