package com.mojian.utils;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mojian.common.RedisConstants;
import com.mojian.entity.SysConfig;
import com.mojian.entity.SysWebConfig;
import com.mojian.mapper.SysConfigMapper;
import com.mojian.mapper.SysWebConfigMapper;
import com.mojian.vo.email.EmailConfigVO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author: quequnlong
 * @date: 2024/12/28
 * @description: 邮箱工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final RedisUtil redisUtil;
    private final SysConfigMapper sysConfigMapper;
    private final SysWebConfigMapper sysWebConfigMapper;

    private JavaMailSenderImpl javaMailSender;
    private EmailConfigVO cachedConfig;

    /**
     * 邮件HTML模板（支持 {siteName} {siteLogo} {siteUrl} {code} 占位符）
     */
    private static final String MAIL_TEMPLATE = "<html><body><div style=\"position:relative;font-size:14px;padding:15px;line-height:1.7;\">"
            + "<div style=\"max-width:800px;padding-bottom:10px;margin:20px auto 0 auto;\">"
            + "<table cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#fff;border-collapse:collapse;border:1px solid #e5e5e5;box-shadow:0 10px 15px rgba(0,0,0,0.05);text-align:left;width:100%;font-size:14px;\">"
            + "<tbody>"
            + "<tr style=\"background-color:#f8f8f8;\"><td>"
            + "<img style=\"padding:15px 0 15px 30px;width:50px\" src=\"{siteLogo}\" />"
            + "<span>{siteName}</span></td></tr>"
            + "<tr><td style=\"padding:30px;\">"
            + "<h1 style=\"font-size:26px;font-weight:bold;color:#00785a;\">验证您的邮箱地址</h1>"
            + "<p style=\"line-height:1.75em;\">感谢您使用 {siteName}.</p>"
            + "<p style=\"line-height:1.75em;\">以下是您的邮箱验证码，请将它输入到 <span style=\"color:#409eff;\">{siteName}</span> 的邮箱验证码输入框中:</p>"
            + "</td></tr>"
            + "<tr><td style=\"padding:0 30px;\">"
            + "<p style=\"color:#253858;text-align:center;line-height:1.75em;background-color:#f2f2f2;min-width:200px;margin:0 auto;font-size:28px;border-radius:5px;border:1px solid #d9d9d9;font-weight:bold;\">{code}</p>"
            + "</td></tr>"
            + "<tr><td style=\"padding:30px;\">"
            + "<p style=\"line-height:1.75em;\">这一封邮件包括一些您的私密信息，请不要回复或转发它，以免带来不必要的信息泄露风险。</p>"
            + "</td></tr>"
            + "<tr><td style=\"padding:30px;\">"
            + "<hr><p style=\"text-align:center;line-height:1.75em;\">"
            + "<a href=\"{siteUrl}\" style=\"text-decoration:none;color:#409eff\">{siteName}</a>"
            + "</p></td></tr>"
            + "</tbody></table></div></div></body></html>";

    /**
     * 获取邮件配置（优先缓存）
     */
    private EmailConfigVO getEmailConfig() {
        Object value = redisUtil.get(RedisConstants.EMAIL_CONFIG_KEY);
        if (value != null) {
            return JSONObject.parseObject(value.toString(), EmailConfigVO.class);
        }
        // 从数据库批量查询
        List<SysConfig> configs = sysConfigMapper.selectList(
            new LambdaQueryWrapper<SysConfig>().likeRight(SysConfig::getConfigKey, "mail_")
        );
        EmailConfigVO config = buildConfigVO(configs);
        redisUtil.set(RedisConstants.EMAIL_CONFIG_KEY,
            JSONObject.toJSONString(config),
            RedisConstants.DAY_EXPIRE, TimeUnit.SECONDS);
        return config;
    }

    /**
     * 从数据库配置列表构建 EmailConfigVO
     */
    private EmailConfigVO buildConfigVO(List<SysConfig> configs) {
        EmailConfigVO.EmailConfigVOBuilder builder = EmailConfigVO.builder();
        for (SysConfig config : configs) {
            switch (config.getConfigKey()) {
                case "mail_smtp_host" -> builder.smtpHost(config.getConfigValue());
                case "mail_smtp_port" -> builder.smtpPort(Integer.parseInt(config.getConfigValue()));
                case "mail_smtp_email" -> builder.smtpEmail(config.getConfigValue());
                case "mail_smtp_password" -> builder.smtpPassword(config.getConfigValue());
                case "mail_subject" -> builder.mailSubject(config.getConfigValue());
            }
        }
        return builder.build();
    }

    /**
     * 构建或复用 JavaMailSender
     */
    private JavaMailSenderImpl getMailSender(EmailConfigVO config) {
        if (javaMailSender != null && config.equals(cachedConfig)) {
            return javaMailSender;
        }
        javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(config.getSmtpHost());
        javaMailSender.setPort(config.getSmtpPort());
        javaMailSender.setUsername(config.getSmtpEmail());
        javaMailSender.setPassword(config.getSmtpPassword());
        javaMailSender.setDefaultEncoding("UTF-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.ssl.enable", "true");
        p.setProperty("mail.debug", "true");
        javaMailSender.setJavaMailProperties(p);
        cachedConfig = config;
        return javaMailSender;
    }

    /**
     * 发送验证码
     */
    public void sendCode(String email) throws MessagingException {
        EmailConfigVO config = getEmailConfig();
        JavaMailSenderImpl sender = getMailSender(config);

        int code = (int) ((ThreadLocalRandom.current().nextDouble() * 9 + 1) * 100000);

        // 从 sys_web_config 获取站点信息
        SysWebConfig webConfig = sysWebConfigMapper.selectOne(
            new LambdaQueryWrapper<SysWebConfig>().last("limit 1")
        );

        // 替换模板占位符
        String content = MAIL_TEMPLATE
            .replace("{code}", String.valueOf(code))
            .replace("{siteName}", webConfig.getName())
            .replace("{siteLogo}", webConfig.getLogo())
            .replace("{siteUrl}", webConfig.getWebUrl());

        // 替换主题占位符
        String subject = config.getMailSubject()
            .replace("{siteName}", webConfig.getName());

        this.send(sender, email, content, subject);
        log.info("邮箱验证码发送成功,邮箱:{},验证码:{}", email, code);

        redisUtil.set(RedisConstants.CAPTCHA_CODE_KEY + email, code + "");
        redisUtil.expire(RedisConstants.CAPTCHA_CODE_KEY + email,
            RedisConstants.MINUTE_EXPIRE, TimeUnit.SECONDS);
    }

    private void send(JavaMailSenderImpl sender, String email, String template, String subject) throws MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper mineHelper = new MimeMessageHelper(mimeMessage, true);
        mineHelper.setSubject(subject);
        mineHelper.setFrom(Objects.requireNonNull(sender.getUsername()));
        mineHelper.setTo(email);
        mineHelper.setSentDate(DateUtil.getNowDate());
        mineHelper.setText(template, true);
        sender.send(mimeMessage);
    }
}
