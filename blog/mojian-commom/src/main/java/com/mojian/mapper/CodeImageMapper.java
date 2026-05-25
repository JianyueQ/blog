package com.mojian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mojian.entity.CodeImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验证码图片库 Mapper接口
 */
@Mapper
public interface CodeImageMapper extends BaseMapper<CodeImage> {
}
