package com.mojian.vo.user;

import com.mojian.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author: quequnlong
 * @date: 2025/1/3
 * @description:
 */
@Data
public class OnlineUserVo extends SysUser {

    @Schema(description = "token")
    private String tokenValue;

}
