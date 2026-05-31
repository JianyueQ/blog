package com.mojian.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mojian.entity.SysMoment;
import com.mojian.mapper.SysMomentMapper;
import com.mojian.service.MomentService;
import com.mojian.utils.PageUtil;
import com.mojian.utils.MarkdownUtils;
import com.mojian.vo.moment.MomentPageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: quequnlong
 * @date: 2025/2/5
 * @description:
 */
@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {

    private final SysMomentMapper baseMapper;

    @Override
    public IPage<MomentPageVo> getMomentList() {
        // 显式调用 BaseMapper 的 selectPage 方法
        IPage<SysMoment> page = ((com.baomidou.mybatisplus.core.mapper.BaseMapper<SysMoment>) baseMapper)
                .selectPage(PageUtil.<SysMoment>getPage(), null);
        
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
}
