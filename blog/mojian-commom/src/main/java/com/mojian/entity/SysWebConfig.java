package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 网站配置表
 */
@Data
@TableName("sys_web_config")
@Schema(name = "网站配置表对象 gen_table")
public class SysWebConfig implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "logo(文件UID)")
    private String logo;

    @Schema(description = "网站名称")
    private String name;

    @Schema(description = "介绍")
    private String summary;

    @Schema(description = "备案号")
    private String recordNum;

    @Schema(description = "网站地址")
    private String webUrl;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "个性签名")
    private String authorInfo;

    @Schema(description = "作者头像")
    private String authorAvatar;

    @Schema(description = "支付宝收款码")
    private String aliPay;

    @Schema(description = "微信收款码")
    private String weixinPay;

    @Schema(description = "github地址")
    private String github;

    @Schema(description = "gitee地址")
    private String gitee;

    @Schema(description = "QQ号")
    private String qqNumber;

    @Schema(description = "QQ群")
    private String qqGroup;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "微信")
    private String wechat;

    @Schema(description = "显示的列表（用于控制邮箱、QQ、QQ群、Github、Gitee、微信是否显示在前端）")
    private String showList;

    @Schema(description = "登录方式列表（用于控制前端登录方式，如账号密码,码云,Github,QQ,微信）")
    private String loginTypeList;

    @Schema(description = "是否开启评论(0:否 1:是)")
    private Integer openComment;

    @Schema(description = "是否开启赞赏(0:否， 1:是)")
    private Integer openAdmiration;

    @Schema(description = "游客头像")
    private String touristAvatar;

    @Schema(description = "公告")
    private String bulletin;

    @Schema(description = "关于我")
    private String aboutMe;

    @Schema(description = "是否开启灯笼")
    private Integer openLantern;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime updateTime;
}
