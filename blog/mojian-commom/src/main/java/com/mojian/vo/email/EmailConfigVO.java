package com.mojian.vo.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮件配置VO（用于Redis缓存）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailConfigVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** SMTP服务器地址 */
    private String smtpHost;

    /** SMTP服务器端口 */
    private Integer smtpPort;

    /** 发件人邮箱 */
    private String smtpEmail;

    /** 邮箱授权密码 */
    private String smtpPassword;

    /** 邮件主题（支持占位符） */
    private String mailSubject;
}
