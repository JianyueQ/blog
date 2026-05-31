package com.mojian.vo.moment;

import com.mojian.entity.SysMoment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: quequnlong
 * @date: 2025/2/5
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MomentPageVo extends SysMoment {

    private String nickname;

    private String avatar;

    /**
     * 渲染后的 HTML 内容（用于前端展示）
     */
    private String htmlContent;

}
