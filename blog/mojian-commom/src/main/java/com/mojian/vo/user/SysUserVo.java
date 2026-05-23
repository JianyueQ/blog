package com.mojian.vo.user;

import com.mojian.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "用户分页视图对象")
public class SysUserVo extends SysUser {

    @Schema(description = "角色id集合")
    private List<Integer> roleIds;
}
