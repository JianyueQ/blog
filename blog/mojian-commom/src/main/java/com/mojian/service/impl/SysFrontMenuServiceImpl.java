package com.mojian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mojian.common.Constants;
import com.mojian.entity.SysFrontMenu;
import com.mojian.exception.ServiceException;
import com.mojian.mapper.SysFrontMenuMapper;
import com.mojian.service.SysFrontMenuService;
import com.mojian.vo.frontMenu.FrontMenuVO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysFrontMenuServiceImpl extends ServiceImpl<SysFrontMenuMapper, SysFrontMenu>
        implements SysFrontMenuService {

    /**
     * 获取管理页面菜单树（全量，不缓存）
     */
    @Override
    public List<SysFrontMenu> getMenuTree() {
        List<SysFrontMenu> menus = list(new LambdaQueryWrapper<SysFrontMenu>()
                .orderByAsc(SysFrontMenu::getSort));

        Map<Integer, List<SysFrontMenu>> childrenMap = menus.stream()
                .filter(m -> m.getParentId() != 0)
                .collect(Collectors.groupingBy(SysFrontMenu::getParentId));

        menus.forEach(m -> m.setChildren(childrenMap.get(m.getId())));

        return menus.stream()
                .filter(m -> m.getParentId() == 0)
                .collect(Collectors.toList());
    }

    /**
     * 获取前台导航菜单列表（公开接口使用）
     * 缓存策略：Spring Cache + Redis，键名 front_menu::nav_list
     */
    @Override
    @Cacheable(cacheNames = Constants.CACHE_FRONT_MENU, key = "'" + Constants.CACHE_FRONT_MENU_NAV + "'")
    public List<FrontMenuVO> getNavList() {
        List<SysFrontMenu> menus = list(new LambdaQueryWrapper<SysFrontMenu>()
                .eq(SysFrontMenu::getStatus, 1)
                .orderByAsc(SysFrontMenu::getSort));
        return buildNavTree(menus);
    }

    /**
     * 新增菜单 — 清除缓存
     */
    @Override
    @CacheEvict(cacheNames = Constants.CACHE_FRONT_MENU, allEntries = true)
    public void addMenu(SysFrontMenu menu) {
        save(menu);
    }

    /**
     * 修改菜单 — 清除缓存
     */
    @Override
    @CacheEvict(cacheNames = Constants.CACHE_FRONT_MENU, allEntries = true)
    public void updateMenu(SysFrontMenu menu) {
        updateById(menu);
    }

    /**
     * 删除菜单 — 清除缓存
     */
    @Override
    @CacheEvict(cacheNames = Constants.CACHE_FRONT_MENU, allEntries = true)
    public void deleteMenu(Integer id) {
        if (count(new LambdaQueryWrapper<SysFrontMenu>()
                .eq(SysFrontMenu::getParentId, id)) > 0) {
            throw new ServiceException("存在子菜单，不能删除");
        }
        removeById(id);
    }

    /**
     * 构建前台导航菜单树形结构（返回 VO）
     */
    private List<FrontMenuVO> buildNavTree(List<SysFrontMenu> menus) {
        // 转换为 VO
        List<FrontMenuVO> voList = menus.stream().map(m -> {
            FrontMenuVO vo = new FrontMenuVO();
            vo.setId(m.getId());
            vo.setTitle(m.getTitle());
            vo.setPath(m.getPath());
            vo.setIcon(m.getIcon());
            vo.setSort(m.getSort());
            vo.setHidden(m.getHidden());
            vo.setIsExternal(m.getIsExternal());
            vo.setColorClass(m.getColorClass());
            return vo;
        }).toList();

        // 构建 id -> parentId 映射
        Map<Integer, Integer> idToParent = new HashMap<>();
        for (SysFrontMenu m : menus) {
            idToParent.put(m.getId(), m.getParentId());
        }

        // 按 parentId 分组
        Map<Integer, List<FrontMenuVO>> childrenMap = new HashMap<>();
        for (int i = 0; i < menus.size(); i++) {
            Integer parentId = menus.get(i).getParentId();
            if (parentId != 0) {
                childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(voList.get(i));
            }
        }

        // 设置 children
        voList.forEach(vo -> vo.setChildren(childrenMap.get(vo.getId())));

        // 返回一级菜单（parentId == 0）
        return voList.stream()
                .filter(vo -> {
                    Integer parentId = idToParent.get(vo.getId());
                    return parentId != null && parentId == 0;
                })
                .collect(Collectors.toList());
    }
}
