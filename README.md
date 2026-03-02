# Auto Mine Helper

一个为 Minecraft 1.12.2 设计的自动化挖矿辅助模组。

## 功能特性

- **自动回家**: 当背包满时自动返回家
- **自动重生**: 死亡后自动复活
- **命令序列**: 执行带延迟的命令序列
- **白名单过滤**: 物品白名单过滤
- **移动检测**: 检测玩家是否长时间未移动
- **GUI 界面**: 按 G 键打开配置界面

## 安装

1. 确保已安装 Minecraft Forge 1.12.2-14.23.5.2859
2. 将编译好的 jar 文件放入 mods 文件夹
3. 启动游戏

## 使用方法

### 命令列表

- `/am help` - 显示帮助
- `/am toggle` - 开关模组
- `/am home` - 返回家
- `/am respawn <on|off>` - 开关自动重生
- `/am movecheck <on|off>` - 开关移动检测
- `/am run` - 执行命令序列
- `/am addcmd <command>` - 添加命令（使用 %N 表示延迟毫秒数）
- `/am clearcmd` - 清空命令序列
- `/am listcmd` - 列出命令
- `/am whitelist <add|remove|list|toggle> [item]` - 管理白名单
- `/am chest <list|set|find> [name|radius]` - 管理箱子
- `/am config <key> <value>` - 设置配置

### GUI 界面

按 `G` 键打开配置界面，可以轻松配置所有设置。

## 构建

由于本项目使用 ForgeGradle 2.3，需要安装 Gradle 4.9。

### 方法一：使用 Gradle Wrapper（推荐）

如果项目包含 Gradle Wrapper：

**Windows:**
```bash
gradlew.bat build
```

**Linux/Mac:**
```bash
./gradlew build
```

### 方法二：手动安装 Gradle

1. 下载 Gradle 4.9: https://gradle.org/releases/
2. 解压并配置环境变量
3. 运行 `gradle build`

### 方法三：使用 IDE

推荐使用 IntelliJ IDEA 或 Eclipse：

1. 导入项目为 Gradle 项目
2. 等待依赖下载完成
3. 运行 Gradle 任务 `build`

构建产物在 `build/libs/` 目录下。

## 依赖要求

- Java 8
- Gradle 4.9
- Minecraft Forge 1.12.2-14.23.5.2859

## 常见问题

**构建失败：**
- 确保网络连接正常（需要下载 Forge 依赖）
- 检查 Java 版本是否为 JDK 8
- 清理构建缓存：`gradle clean`

## 许可证

本项目仅供学习交流使用。
