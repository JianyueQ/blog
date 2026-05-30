package com.mojian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mojian.entity.SysFrontMenu;
import com.mojian.vo.frontMenu.FrontMenuVO;

import java.util.List;

/**
 * 前台菜单服务接口
 */
public interface SysFrontMenuService extends IService<SysFrontMenu> {

    /**
     * 获取管理页面菜单树（全量，不缓存）
     */
    List<SysFrontMenu> getMenuTree();

    /**
     * 获取前台导航菜单列表（公开接口，含 Redis 缓存）
     */
    List<FrontMenuVO> getNavList();

    /**
     * 新增菜单
     */
    void addMenu(SysFrontMenu menu);

    /**
     * 修改菜单
     */
    void updateMenu(SysFrontMenu menu);

    /**
     * 删除菜单
     */
    void deleteMenu(Integer id);
}
