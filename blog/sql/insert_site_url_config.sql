-- 新增站点URL配置，将硬编码的域名提取为可配置项
-- 注意：以下值为生产环境默认值，测试环境请将 front_base_url 改为测试域名（如 https://gp.jianyue.cloud）
INSERT INTO sys_config (config_name, config_key, config_value, config_type, remark)
VALUES ('后台站点基础URL', 'admin_base_url', 'https://blog.jianyue.cloud', 'Y', '后台管理系统的基础URL，用于第三方登录回调重定向等场景');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, remark)
VALUES ('前台站点基础URL', 'front_base_url', 'https://blog.jianyue.cloud', 'Y', '前台用户端的基础URL，用于第三方登录回调重定向等场景');
