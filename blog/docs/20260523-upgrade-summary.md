# Spring Boot 升级报告

**日期**: 2026-05-23  
**分支**: `upgrade/sb3-3.5` (从 `dev` 分支创建)  
**原始版本**: Spring Boot 2.7.0 → **目标版本**: Spring Boot 3.5.14  
**JDK**: 17 (OpenJDK 17.0.18)

## 升级概述

成功将 blog 项目从 Spring Boot 2.7.0 升级到 3.5.14，使用 OpenRewrite 自动化迁移工具完成主要代码迁移。

## 执行的迁移步骤

### 1. 前期准备
- 切换到 JDK 17 (原使用 JDK 25 EA 版本，与 Lombok 不兼容)
- 创建工作分支 `upgrade/sb3-3.5`

### 2. OpenRewrite 自动化迁移
执行 recipe: `org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_5`

**自动迁移内容**:
- Spring Boot 父 POM 版本 2.7.0 → 3.5.14
- Maven Surefire Plugin 2.22.1 → 3.1.2
- `javax.*` → `jakarta.*` 命名空间迁移 (100+ 文件)
- Controller 层注解标准化
- 依赖版本对齐

### 3. 编译验证
```
mvn clean compile
```
**结果**: ✅ BUILD SUCCESS (所有 8 个模块编译通过)

## 变更统计

| 模块 | 变更文件数 |
|------|-----------|
| mojian-admin | ~40 文件 |
| mojian-api | ~20 文件 |
| mojian-auth | ~10 文件 |
| mojian-commom | ~5 文件 |
| 其他模块 | ~5 文件 |
| **总计** | **~80+ 文件** |

## 关键依赖版本变更

| 依赖 | 原版本 | 新版本 |
|------|--------|--------|
| Spring Boot | 2.7.0 | 3.5.14 |
| Spring Framework | ~5.3.x | ~6.1.x |
| Spring Security | ~5.7.x | ~7.0.x |
| javax → jakarta | 9.x | 10.x |

### 5. Knife4j API文档
- **原版本**: `knife4j-openapi2-spring-boot-starter` 4.4.0 (使用 javax)
- **新版本**: `knife4j-openapi3-jakarta-spring-boot-starter` 4.5.0 (使用 jakarta)

### 6. MySQL 驱动
- **原版本**: `mysql-connector-java`
- **新版本**: `mysql-connector-j` 8.0.33 (Spring Boot 3 推荐)

### 7. FastJSON
- **原版本**: `com.alibaba:fastjson` 2.0.26
- **新版本**: `com.alibaba.fastjson2:fastjson2` 2.0.62 (更名，Spring Boot 3 兼容)

## 遗留项 (需手动检查)

### 1. Sa-Token 认证框架
当前版本 `1.39.0`，Spring Boot 3 建议升级到 `1.39.0+` (需验证兼容性)

### 2. MyBatis-Plus 3.5.2
当前版本支持 Spring Boot 3，需确认是否需要升级到更高版本以获得最佳性能

### 3. 第三方库兼容性
- JustAuth 1.16.7 (需检查 Spring Boot 3 兼容性)
- volcengine-java-sdk-ark-runtime (需验证)

### 4. 剩余 javax 引用
仅剩 `javax.imageio.ImageIO` (标准 Java API，无需修改)

## Git 提交

```
ad326c5 fix: upgrade knife4j to Spring Boot 3 compatible version (jakarta)
eaecbb2 docs: add Spring Boot 3.5 upgrade summary report
0e839eb chore(rewrite): Spring Boot 2.7 → 3.5 migration via OpenRewrite
```

---

**状态**: ✅ 升级完成，编译通过，运行需验证