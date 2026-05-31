package com.mojian.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Service;
import com.mojian.mapper.SysMomentMapper;
import com.mojian.entity.SysMoment;
import com.mojian.service.SysMomentService;
import com.mojian.utils.PageUtil;
import com.mojian.utils.MarkdownUtils;
import com.mojian.vo.moment.MomentPageVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 说说 服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysMomentServiceImpl extends ServiceImpl<SysMomentMapper, SysMoment> implements SysMomentService {

    /**
     * 查询说说分页列表
     */
    @Override
    public IPage<MomentPageVo> selectPage(SysMoment sysMoment) {
        IPage<SysMoment> page = page(PageUtil.<SysMoment>getPage(), new LambdaQueryWrapper<SysMoment>()
                .orderByDesc(SysMoment::getCreateTime));
        
        // 转换为 VO 并渲染 HTML
        List<MomentPageVo> voList = page.getRecords().stream().map(moment -> {
            MomentPageVo vo = new MomentPageVo();
            vo.setId(moment.getId());
            vo.setUserId(moment.getUserId());
            vo.setContent(moment.getContent());
            vo.setImages(moment.getImages());
            vo.setCreateTime(moment.getCreateTime());
            // Markdown 转 HTML
            vo.setHtmlContent(MarkdownUtils.toHtml(moment.getContent()));
            return vo;
        }).collect(Collectors.toList());
        
        // 构建新的分页结果
        Page<MomentPageVo> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Object add(SysMoment sysMoment) {
        sysMoment.setUserId(StpUtil.getLoginIdAsLong());
        return save(sysMoment);
    }
}
