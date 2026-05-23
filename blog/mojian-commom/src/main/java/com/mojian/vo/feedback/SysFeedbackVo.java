package com.mojian.vo.feedback;

import com.mojian.entity.SysFeedback;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: quequnlong
 * @date: 2025/1/12
 * @description:
 */
@Data
@Schema(name = "反馈对象vo")
public class SysFeedbackVo extends SysFeedback {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;
}
