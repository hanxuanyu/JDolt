# JDolt

JDolt 是一个面向 [Dolt](https://www.dolthub.com/) 的 Java 客户端库，Dolt 是一款具备 Git 风格版本控制能力的 SQL 数据库。JDolt 提供了高级 API，方便你在 Java 应用中对 Dolt 数据库进行版本控制操作，实现类似于 Git 对代码的管理。

## 项目概述

JDolt 将 Dolt 的版本控制功能封装为 Java API，便于在 Java 应用中集成数据库版本管理。支持如 commit、branch、merge、pull、push 等常见版本控制操作。

## 模块说明

本项目包含两个主要模块：

1. **jdolt-core**：核心功能库，负责与 Dolt 数据库交互，包括：
   - API 接口与实现
   - 版本控制操作
   - 数据库连接管理

2. **jdolt-service**：基于 Spring Boot 的服务层，封装 jdolt-core 的功能。

## 主要特性

- 分支管理（创建、列举、删除）
- 提交变更
- 分支/提交检出
- 分支合并
- 拉取与推送
- 重置与回滚
- 冲突解决
- 差异与状态查看
- 以及更多

## 快速开始

### 环境要求

- Java 17 及以上
- Maven
- 可访问的 Dolt 数据库

### 安装

1. 克隆仓库：
   ```
   git clone https://github.com/hanxuanyu/JDolt.git
   ```

2. 使用 Maven 构建：
   ```
   cd JDolt
   mvn clean install
   ```

### 配置

在你的应用中配置 Dolt 数据库连接。如果使用 jdolt-service 模块，可在 `application.yaml` 文件中配置：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: your_username
    password: your_password
    url: jdbc:mysql://your_dolt_server:port/your_database
```

### 使用示例

以下为 JDolt 在 Java 应用中的简单用法：

```java
// 使用 DataSource 初始化 DoltClient
DataSource dataSource = // 你的 DataSource 实现
DoltClient doltClient = DoltClient.initialize(dataSource);

// 获取版本控制 API
VersionControl versionControl = doltClient.versionControl();

// 创建新分支
versionControl.doltBranch().prepare()
    .createBranch("new-feature")
    .execute();

// 检出新分支
versionControl.doltCheckout().prepare()
    .branch("new-feature")
    .execute();

// 数据库变更后提交
versionControl.doltCommit().prepare()
    .message("Added new feature")
    .execute();
```

## 项目结构

```
JDolt/
├── doc/
│   └── codeTemplates/       # Dolt 组件代码模板
├── jdolt-core/              # 核心库模块
│   └── src/
│       ├── main/
│       │   └── java/
│       │       └── com/
│       │           └── hxuanyu/
│       │               └── jdolt/
│       │                   ├── core/       # 核心功能
│       │                   ├── manager/    # 连接管理
│       │                   └── util/       # 工具类
│       └── test/           # 单元测试
├── jdolt-service/          # Spring Boot 服务模块
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── hxuanyu/
│       │   │           └── jdolt/
│       │   │               └── DoltManagerApplication.java
│       │   └── resources/
│       │       └── application.yaml
│       └── test/           # 单元测试
└── pom.xml                 # Maven 父 POM
```

## 依赖引入

你可以直接通过 Maven Central 引入 JDolt：

```xml
<dependency>
    <groupId>com.hxuanyu.doltmanager</groupId>
    <artifactId>jdolt-core</artifactId>
    <version>1.0.1</version>
</dependency>
```

<p align="center">
  <a href="https://search.maven.org/artifact/com.hxuanyu.doltmanager/jdolt-core"><img src="https://img.shields.io/maven-central/v/com.hxuanyu.doltmanager/jdolt-core.svg?label=Maven%20Central" alt="Maven Central"></a>
  <a href="https://github.com/hanxuanyu/JDolt/actions"><img src="https://github.com/hanxuanyu/JDolt/workflows/CI/badge.svg" alt="Build Status"></a>
  <a href="https://github.com/hanxuanyu/JDolt/blob/main/LICENSE"><img src="https://img.shields.io/github/license/hanxuanyu/JDolt.svg" alt="License"></a>
  <a href="https://github.com/hanxuanyu/JDolt/stargazers"><img src="https://img.shields.io/github/stars/hanxuanyu/JDolt?style=social" alt="GitHub stars"></a>
</p>

## 标签

`Java` `Dolt` `数据库` `版本控制` `Maven Central` `客户端库` `Git风格` `开源` `SQL` `数据同步`

## 文档

- [English Documentation](./README.md) | 中文文档

---

> 更多信息、用法示例和高级特性，请参见 [Wiki](https://github.com/hanxuanyu/JDolt/wiki) 或 [English Documentation](./README.md)。
