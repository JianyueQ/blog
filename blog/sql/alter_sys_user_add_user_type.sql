-- 用户类型隔离：新增 user_type 字段
-- 默认值为 1（后台用户），确保存量用户不受影响
ALTER TABLE sys_user
ADD COLUMN user_type TINYINT NOT NULL DEFAULT 1
COMMENT '用户类型 0:前台用户 1:后台用户'
AFTER status;
