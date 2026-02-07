# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

**DataAgent** 是基于 Spring AI Alibaba Graph 的企业级智能数据分析 Agent，支持 Text-to-SQL、Python 深度分析、智能报告生成和 MCP 服务器集成。

## 系统环境

- **操作系统**: Windows（项目运行在 Windows 上）
- **Java 版本**: 17+
- **Node.js 版本**: 16+
- **数据库**: MySQL 5.7+
- **构建工具**: Maven / npm
- **核心框架**: Spring Boot 3.4.8+, Spring AI Alibaba 1.1.0.0

## 常用命令

### 后端构建和运行

```bash
# 清理项目
cd data-agent-management
mvn clean

# 编译项目
mvn compile

# 打包项目（跳过测试）
mvn package -DskipTests

# 运行测试
mvn test

# 启动后端服务
mvn spring-boot:run

# 或使用 IDE 运行启动类
# com.alibaba.cloud.ai.dataagent.DataAgentApplication
```

### 前端构建和运行

```bash
# 进入前端目录
cd data-agent-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview

# 代码检查和修复
npm run lint

# 类型检查
npm run type-check
```

### 代码质量工具

```bash
# 后端代码格式化（Spotless）
mvn spotless:apply

# 后端代码检查（CheckStyle）
mvn checkstyle:check

# 后端 Spring JavaFormat
mvn spring-javaformat:apply
```

## 项目架构

### 模块结构

```
DataAgent/
├── data-agent-management/          # 后端 Spring Boot 应用
│   ├── src/main/java/com/alibaba/cloud/ai/dataagent/
│   │   ├── DataAgentApplication.java    # 启动类
│   │   ├── annotation/                  # 自定义注解
│   │   ├── aop/                        # 切面
│   │   ├── bo/                         # 业务对象
│   │   ├── config/                     # 配置类
│   │   ├── connector/                  # 数据源连接器
│   │   ├── controller/                 # REST 控制器
│   │   ├── converter/                  # 转换器
│   │   ├── dto/                        # 数据传输对象
│   │   ├── entity/                     # 实体类
│   │   ├── enums/                      # 枚举类
│   │   ├── event/                      # 事件类
│   │   ├── exception/                  # 异常处理
│   │   ├── mapper/                     # MyBatis Mapper
│   │   ├── prompt/                     # 提示词配置
│   │   ├── properties/                 # 配置属性
│   │   ├── service/                    # 服务层
│   │   ├── splitter/                   # 文档分割
│   │   ├── strategy/                   # 策略模式
│   │   ├── util/                       # 工具类
│   │   ├── vo/                         # 视图对象
│   │   └── workflow/                   # 工作流
│   │       ├── dispatcher/             # 调度器
│   │       └── node/                   # StateGraph 节点
│   └── src/main/resources/
│       ├── sql/                        # 数据库初始化脚本
│       └── application.yml             # 主配置文件
│
└── data-agent-frontend/              # 前端 Vue 3 应用
    ├── src/
    │   ├── api/                        # API 接口
    │   ├── assets/                     # 静态资源
    │   ├── components/                 # 公共组件
    │   ├── composables/                # 组合式函数
    │   ├── layouts/                    # 布局组件
    │   ├── router/                     # 路由配置
    │   ├── stores/                     # 状态管理
    │   └── views/                      # 页面组件
    └── package.json
```

### StateGraph 工作流架构

系统核心基于 Spring AI Alibaba 的 StateGraph 实现，包含以下关键节点：

**检索阶段**:
- `IntentRecognitionNode` - 意图识别
- `EvidenceRecallNode` - 证据召回（RAG 检索）
- `QueryEnhanceNode` - 查询增强
- `SchemaRecallNode` - 模式召回（表结构检索）
- `TableRelationNode` - 表关系分析

**规划阶段**:
- `FeasibilityAssessmentNode` - 可行性评估
- `PlannerNode` - 计划生成
- `HumanFeedbackNode` - 人工反馈（可选）
- `PlanExecutorNode` - 计划执行

**执行阶段**:
- `SqlGenerateNode` - SQL 生成
- `SemanticConsistencyNode` - 语义一致性检查
- `SqlExecuteNode` - SQL 执行
- `PythonGenerateNode` - Python 代码生成
- `PythonExecuteNode` - Python 执行
- `PythonAnalyzeNode` - Python 分析

**输出阶段**:
- `ReportGeneratorNode` - 报告生成

### 分层架构

1. **控制层 (controller)**: RESTful API 控制器，处理 HTTP 请求
2. **服务层 (service)**: 业务逻辑实现，包含各种服务
3. **数据层 (mapper)**: MyBatis Mapper，负责数据访问
4. **工作流层 (workflow)**: StateGraph 节点，定义分析流程

### 核心服务

- **GraphService**: 工作流编排和执行
- **MultiTurnContextManager**: 多轮对话上下文管理
- **AgentVectorStoreService**: 向量检索服务
- **AiModelRegistry**: 多模型管理和热切换
- **CodePoolExecutorService**: Python 代码执行（Docker/Local）
- **McpServerService**: MCP 服务器集成

## 核心架构模式

### 1. StateGraph 工作流模式

基于 Spring AI 的 StateGraph 实现复杂的数据分析工作流，每个节点负责特定的处理步骤，通过状态机管理整个分析流程。

### 2. 人类反馈机制 (Human-in-the-loop)

- 运行时请求参数 `humanFeedback=true` 启用
- `PlanExecutorNode` 检测 `HUMAN_REVIEW_ENABLED`，转入 `HumanFeedbackNode`
- `CompiledGraph` 使用 `interruptBefore(HUMAN_FEEDBACK_NODE)` 实现暂停与恢复
- 同意继续执行；拒绝则回到 `PlannerNode` 重新规划

### 3. RAG 检索增强

- 查询重写: `EvidenceRecallNode` 调用 LLM 生成独立检索问题
- 召回通道: `AgentVectorStoreService` 执行向量检索；可选混合检索
- 文档类型: 业务知识 + 智能体知识，按元数据过滤

### 4. 多模型调度

通过 `AiModelRegistry` 实现多模型管理和热切换：
- 支持动态创建和切换 Chat 模型和 Embedding 模型
- 通过 `ModelConfig*` 配置模型
- 运行时可以切换不同的 LLM 提供商

### 5. 策略模式

- 检索策略: 支持多种向量检索策略
- 代码执行策略: 支持 Docker、Local 和 AI 模拟三种执行环境
- 模型调度策略: 支持多种 LLM 模型动态切换

## 配置管理

### 配置前缀

所有配置项均位于 `spring.ai.alibaba.data-agent` 前缀下。

### 关键配置

**通用配置**:
- `llm-service-type`: LLM 服务类型 (STREAM/BLOCK)
- `max-sql-retry-count`: SQL 执行失败重试次数
- `max-sql-optimize-count`: SQL 优化最多次数
- `sql-score-threshold`: SQL 优化分数阈值
- `maxturnhistory`: 最多保留的对话轮数
- `fusion-strategy`: 多路召回结果融合策略 (rrf)

**向量库配置** (`vector-store`):
- `default-similarity-threshold`: 全局默认相似度阈值
- `table-similarity-threshold`: 召回表的相似度阈值
- `default-topk-limit`: 查询返回的最大文档数量
- `table-topk-limit`: 召回表的最大文档数量
- `enable-hybrid-search`: 是否启用混合搜索

**代码执行器配置** (`code-executor`):
- `code-pool-executor`: 执行器类型 (DOCKER/LOCAL/AI_SIMULATION)
- `image-name`: Docker 镜像名称
- `code-timeout`: Python 代码执行超时时间
- `container-timeout`: 容器最大运行时长

**文件存储配置** (`file`):
- `type`: 存储类型 (LOCAL/OSS)
- `path`: 本地上传目录路径
- `url-prefix`: 对外暴露的访问前缀

## 数据库初始化

```bash
# 导入数据库结构
mysql -u root -p < data-agent-management/src/main/resources/sql/schema.sql

# 导入初始化数据（如果有）
mysql -u root -p < data-agent-management/src/main/resources/sql/data.sql
```

## 访问地址

- **前端应用**: `http://localhost:3000`
- **后端 API**: `http://localhost:8065`
- **API 文档**: `http://localhost:8065/swagger-ui.html`
- **MCP 服务器**: 通过 Spring AI MCP Server Boot Starter 提供

## 代码规范

### Java 编码规范

1. **命名规范**:
   - 类名: 大驼峰命名法 (PascalCase)
   - 方法名: 小驼峰命名法 (camelCase)
   - 常量: 全大写下划线分隔 (UPPER_SNAKE_CASE)

2. **注释规范**:
   - 所有公共类和方法必须有 JavaDoc 注释
   - 复杂逻辑需要添加行内注释

3. **代码格式**:
   - 使用 4 个空格缩进
   - 每行代码不超过 120 字符
   - 使用 Google Java Style Guide

### TypeScript/Vue 编码规范

1. **命名规范**:
   - 组件名: 大驼峰命名法
   - 变量/函数: 小驼峰命名法
   - 接口: I 前缀 + 大驼峰命名法

2. **类型定义**:
   - 优先使用 interface 而非 type
   - 避免使用 any 类型
   - 为所有函数参数和返回值添加类型

3. **代码格式**:
   - 使用 2 个空格缩进
   - 使用 Prettier 格式化代码
   - 使用 ESLint 检查代码质量

## 数据访问层

- **框架**: MyBatis 3.0.4 + MyBatis Plus
- **连接池**: Druid
- **Mapper 接口**: 位于 `mapper` 包
- **XML 映射**: 位于 `resources/mapper`（如果有）

## 特殊功能

### MCP 服务器支持

原生支持 Model Context Protocol，可作为 MCP 服务器集成到 Claude Desktop 等工具中。相关实现:
- `McpServerService`: MCP 服务器服务
- `nl2SqlToolCallback`: NL2SQL 工具
- `listAgentsToolCallback`: 智能体列表工具

### 多数据源支持

支持以下数据库:
- MySQL
- PostgreSQL
- H2
- 达梦 (Dameng)
- SQL Server

### Python 执行器

支持三种执行模式:
1. **Docker**: 使用 Docker 容器执行 Python 代码（默认）
2. **Local**: 本地环境执行
3. **AI Simulation**: AI 模拟执行结果

### 流式输出

- 通过 `GraphController` SSE + `GraphServiceImpl` 实现流式处理
- 使用 `TextType` 标记 SQL/JSON/HTML/Markdown，前端据此渲染
- 支持运行时停止流式处理

## 相关文档

- [README.md](../README.md) - 项目介绍
- [docs/QUICK_START.md](../docs/QUICK_START.md) - 快速开始
- [docs/ARCHITECTURE.md](../docs/ARCHITECTURE.md) - 架构设计
- [docs/DEVELOPER_GUIDE.md](../docs/DEVELOPER_GUIDE.md) - 开发者指南
- [docs/ADVANCED_FEATURES.md](../docs/ADVANCED_FEATURES.md) - 高级功能
- [docs/KNOWLEDGE_USAGE.md](../docs/KNOWLEDGE_USAGE.md) - 知识配置最佳实践

## 启动类

- **后端**: `com.alibaba.cloud.ai.dataagent.DataAgentApplication`
- **配置文件**: `data-agent-management/src/main/resources/application.yml`
