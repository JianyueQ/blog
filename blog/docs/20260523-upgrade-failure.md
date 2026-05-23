# Spring Boot 2.7 → 3.5 升级失败报告

**日期**: 2026-05-23  
**原始分支**: dev  
**工作分支**: upgrade/sb3-3.5 (已创建)  
**目标版本**: Spring Boot 3.5.x, JDK 21

---

## 1. 升级阶段

### Stage 1: 基线检查 ✅ 完成
- 项目确认使用 Spring Boot 2.7.0
- 已创建工作分支 `upgrade/sb3-3.5`
- 配置了 lombok.version=1.18.46

### Stage 2: 基线编译修复 ⚠️ 失败
- 未能解决 Lombok 注解处理器在 JDK 25 下的问题
- 所有 @Data, @Builder, @RequiredArgsConstructor 等注解生成的代码无法被识别

### Stage 3-6: 未执行
- 因基线编译失败，未进行 OpenRewrite 迁移

---

## 2. 错误总结

**主要问题**: 项目在 JDK 25 (openjdk 25.0.3-ea) 环境下 Lombok 注解处理器不工作。

**错误信息**:
```
cannot find symbol
  symbol: method getCode()
  location: variable type of type com.mojian.enums.MenuTypeEnum
```

**影响的注解**: @Data, @Builder, @AllArgsConstructor, @NoArgsConstructor, @RequiredArgsConstructor, @Slf4j

**影响的文件 (示例)**:
- com.mojian.common.Result
- com.mojian.common.PageQuery
- com.mojian.dto.user.LoginUserInfo
- com.mojian.entity.SysArticle
- com.mojian.entity.SysNotifications
- 等所有使用 Lombok 注解的类

---

## 3. 尝试的解决方案

| 方案 | 操作 | 结果 |
|------|------|------|
| 升级 lombok 版本 | lombok.version 1.18.34 → 1.18.36 → 1.18.46 | 失败 |
| 添加 annotationProcessorPaths | maven-compiler-plugin 配置 | 导致 ExceptionInInitializerError |
| 添加 lombok 到 plugin dependencies | maven-compiler-plugin dependencies | 失败 |
| 移除 scope=provided | 保持 compile scope | 失败 |
| 升级 maven-compiler-plugin | - | 未测试 |

---

## 4. 下一步建议

### 方案 A: 使用兼容的 JDK 版本 (推荐)
1. 安装 JDK 17 或 JDK 21 LTS 版本
2. 配置 JAVA_HOME 指向兼容版本
3. 在 Windows WSL 中切换 JDK:
   ```bash
   # 查看可用的 JDK
   update-alternatives --list java
   
   # 选择 JDK 17
   sudo update-alternatives --config java
   ```
4. 重新运行 `mvn clean compile`

### 方案 B: 手动生成 getters/setters
1. 如果无法切换 JDK，考虑手动添加必要的 getter/setter 方法
2. 这是一个耗时且容易出错的工作，不推荐

### 方案 C: 等待 Lombok 更新
1. 关注 Lombok 官方对 JDK 25+ 的支持进度
2. 等待新的 Lombok 版本发布后再次尝试

---

## 5. 代码变更记录

已应用的变更 (在 upgrade/sb3-3.5 分支):
- mojian-commom/pom.xml: maven.compiler.source/target 从 8 改为 17
- mojian-commom/pom.xml: lombok 版本改为 1.18.46
- mojian-commom/pom.xml: 移除 scope=provided
- pom.xml: 添加 lombok.version=1.18.46 属性
- pom.xml: 添加 lombok 到 maven-compiler-plugin dependencies

**注意**: 这些变更在当前 JDK 25 环境下不工作，需要在兼容的 JDK 版本下验证。

---

## 6. 验证命令 (切换 JDK 后)

```bash
# 验证编译
cd /mnt/c/Users/31373/Desktop/shiyi-blog/blog
mvn clean compile

# 如果成功，运行 OpenRewrite 迁移
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-spring:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_5

# 验证迁移后的编译
mvn clean compile
```

---

## 7. 相关文件

- 根 POM: `c:\Users\31373\Desktop\shiyi-blog\blog\pom.xml`
- 公共模块: `c:\Users\31373\Desktop\shiyi-blog\blog\mojian-commom\pom.xml`
- Lombok 依赖检查:
  ```bash
  mvn dependency:tree -pl mojian-commom | grep lombok
  ```

---

**报告生成时间**: 2026-05-23