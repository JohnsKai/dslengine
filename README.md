# AI 辅助 IoT 规则引擎开发实践总结
项目耗时2小时左右
本项目是一个 **IoT 规则引擎**，支持 DSL 配置、事件驱动、MQTT 控制设备，要求 JDK 8 兼容、TPS ≥ 800。全程使用 **Cursor + GPT-4** 完成设计、编码、测试。以下为提示词技巧、Rules 配置等经验总结。

## 目录
- [提示词工程技巧](#提示词工程技巧)
- [Cursor Rules 配置](#cursor-rules-配置)
- [开发流程与协作模式](#开发流程与协作模式)
- [性能测试与压测](#性能测试与压测)
- [Git 与远程仓库操作](#git-与远程仓库操作)
- [JDK 8 兼容性注意事项](#jdk-8-兼容性注意事项)
- [经验总结与避坑指南](#经验总结与避坑指南)
- [附录：常用 Cursor 快捷键](#附录常用-cursor-快捷键)

## 提示词工程技巧
**1. 分阶段、模块化提示词**  
按骨架 → 核心接口 → DTO → 节点实现 → 引擎 → 测试 拆分任务。  

一定要先设计，可以先和ai讨论自己的架构设计(白嫖ds)，然后一步步演化，最终得到一个自己相对满意的设计，然后自己先大概敲一下提示词，甚至可以直接让ai给出提示词，觉得没什么问题，就无脑交给cursor做就行了
示例：`任务1：创建 Maven 项目，pom.xml 包含 MQTT、Aviator、Jackson、SLF4J，JDK 8。`

**2. 使用约束性语言，避免歧义**  
使用“必须”、“不要”等强约束词，明确列出禁止的 API  
示例：`不要使用 Map<String,Object> 作为上下文；必须用 TypedContext+Key<T>。禁止 Java 9+ 语法。`

**3. 给出输入输出示例**  
提供 JSON 示例和期望的 DTO 结构。  
示例：`规则 JSON：{ "id":"lightRule", "startNodeId":"onEvent", "nodes":[...] } 请生成 RuleDefinition 类。`

**4. 要求只输出变更代码块（diff 风格）**  
节省 token，避免重复大段代码。  
示例：`只输出修改的方法，不要重复整个类。`

**5. 使用 Cursor 的 `@` 引用文件**  
在 Composer/Chat 中用 `@文件名` 让 AI 直接读取现有代码。还有其他@ 用法也是同此。
示例：`@TypedContext.java 增加 toMap() 方法。`

**6. 迭代修复：把错误信息直接喂给 AI**  
复制完整错误栈，让 AI 分析修复。  
示例：`编译错误：class file version 55.0 ... only recognizes 52.0。如何解决？`

## Cursor Rules 配置
这个很重要，一定要确认自己的技术栈，一开始默认用了jdk17，结果本地只有jdk8，来回修改了几次，造成了一些没必要的工作。一些避免事项按需写就行，一开始如果确定好自己的技术栈，都不需要写，节省上下文
项目根目录 `.cursorrules` 固化规范。有效字段示例：
```text
# 项目背景
技术栈：JDK 8 + Maven，IoT 规则引擎，目标 TPS ≥ 800。

# 代码规范（这个可以事先确认）
- 所有 public 类必须有 Javadoc。
- 使用 SLF4J 日志，不用 System.out。
- 异常处理：自定义异常直接抛出，其他记录 error。

# 避免事项
- 禁止 var、List.of、Path.of、Files.readString 等 Java 9+ API。
- 不要使用 Map<String,Object> 作为上下文。
- MQTT 必须异步，禁止在 onEvent 中同步等待。

# 节省 token 要求
- 回复时只生成必要的代码，不要解释整个文件。
- 修改已有类时只输出 diff。
