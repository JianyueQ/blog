-- =============================================
-- 前台菜单图标系统升级 - 支持明暗模式独立图标
-- 执行日期: 2026-05-30
-- =============================================

-- 1. 添加 icon_dark 字段（暗色模式图标）
ALTER TABLE `sys_front_menu` 
ADD COLUMN `icon_dark` VARCHAR(100) DEFAULT NULL COMMENT '图标类名（暗色模式）' AFTER `icon`;

-- 2. 数据迁移：根据现有 icon 字段生成对应的 icon_dark 值
-- 规则：front/home -> front-dark/home-dark
UPDATE `sys_front_menu` 
SET `icon_dark` = CONCAT(
  'front-dark/',
  SUBSTRING_INDEX(`icon`, '/', -1),
  '-dark'
)
WHERE `icon` LIKE 'front/%' AND `icon_dark` IS NULL;

-- 3. 查看迁移结果
SELECT id, title, icon, icon_dark FROM `sys_front_menu` ORDER BY sort;
