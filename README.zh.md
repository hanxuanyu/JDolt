# JDolt

JDolt 是一个用于 [Dolt](https://www.dolthub.com/) 的 Java 客户端库，Dolt 是一个具有类似 Git 版本控制功能的 SQL 数据库。该项目提供了一个高级 API，用于与 Dolt 数据库交互，使您能够对数据库执行类似于对代码进行 Git 操作的版本控制操作。

## 项目概述

JDolt 将 Dolt 的版本控制功能封装到 Java API 中，使数据库版本控制轻松集成到您的 Java 应用程序中。它提供了常见版本控制操作的方法，如提交、分支、合并、拉取、推送等。

## 模块

该项目由两个主要模块组成：

1. **jdolt-core**：包含与 Dolt 数据库交互的核心功能，包括：
   - API 接口和实现
   - 版本控制操作
   - 数据库连接管理

2. **jdolt-service**：一个 Spring Boot 应用程序，在 jdolt-core 之上提供服务层。

## 功能

JDolt 支持广泛的 Dolt 版本控制操作，包括：

- 分支管理（创建、列出、删除）
- 提交更改
- 检出分支或提交
- 合并分支
- 拉取和推送更改
- 重置和恢复更改
- 冲突解决
- 差异和状态操作
- 以及更多功能

## 入门指南

### 先决条件

- Java 17 或更高版本
- Maven
- 访问 Dolt 数据库

### 安装

1. 克隆仓库：
   ```
   git clone https://github.com/yourusername/JDolt.git
   ```

2. 使用 Maven 构建项目：
   ```
   cd JDolt
   mvn clean install
   ```

### 配置

在您的应用程序中配置 Dolt 数据库连接。如果使用 jdolt-service 模块，您可以在 `application.yaml` 文件中进行配置：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: your_username
    password: your_password
    url: jdbc:mysql://your_dolt_server:port/your_database
```

### 使用示例

以下是在 Java 应用程序中使用 JDolt 的简单示例：

```java
// 使用 DataSource 初始化 DoltClient
DataSource dataSource = // 您的 DataSource 实现
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

// 对数据库进行更改后，提交它们
versionControl.doltCommit().prepare()
    .message("添加新功能")
    .execute();
```

## 项目结构

```
JDolt/
├── doc/
│   └── codeTemplates/       # 创建 Dolt 组件的代码模板
├── jdolt-core/              # 核心库模块
│   └── src/
│       ├── main/
│       │   └── java/
│       │       └── com/
│       │           └── hxuanyu/
│       │               └── jdolt/
│       │                   ├── core/       # 核心功能
│       │                   ├── manager/    # 连接管理
│       │                   └── util/       # 实用工具类
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

## 贡献

欢迎贡献！请随时提交拉取请求。

## 许可证

该项目根据 [您的许可证] 许可 - 有关详细信息，请参阅 LICENSE 文件。

## 致谢

- [Dolt](https://www.dolthub.com/) - 具有类似 Git 版本控制的 SQL 数据库
- 该项目的开发人员和贡献者