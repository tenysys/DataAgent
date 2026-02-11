# Dify 知识库集成 - 后端实施计划

## Context

将现有的智能体知识和业务知识（同义词库）扩展支持 Dify 知识库，允许用户从 Dify 知识库列表中选择知识库并与智能体绑定，在 EvidenceRecallNode 中实现 Dify 知识库的检索功能。

---

## 实施步骤

### 步骤 1: 创建 Dify 配置类

**目标**: 实现 Dify API 的配置管理

- 创建 `DifyProperties.java` 配置属性类（API 地址、Token）
- 在 `application.yml` 添加 Dify 配置项

**配置项**:
```yaml
spring:
  ai:
    alibaba:
      data-agent:
        dify:
          api-url: http://172.18.0.9:4008
          api-token: dataset-9Jj1gUdI20pf4nU1ilmQlcGg
```

---

### 步骤 2: 创建数据库实体和 Mapper

**目标**: 实现智能体与 Dify 知识库绑定的数据层

- 创建 `AgentDatasetBinding.java` 实体类
- 创建 `AgentDatasetBindingMapper.java` 接口

**表结构** (已存在于 `agent_dataset_binding` 表):

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | int(11) | 主键id，自增 |
| agent_id | int(11) | 智能体id |
| dataset_id | varchar(64) | 知识库id |
| dataset_name | varchar(256) | 知识库名称 |
| indexing_technique | varchar(64) | 索引方式 |
| search_method | varchar(64) | 检索方式 |

---

### 步骤 3: 创建 DTO/QO 类

**目标**: 定义 API 请求和响应的数据模型

#### 3.1 查询对象 (QO)

| 类名 | 用途 | 字段 |
|------|------|------|
| `DifyPageQo` | 分页查询请求 | keyword, page, limit |
| `DifyAgentKnowledgeQo` | 添加知识库请求 | agentId, datasetId, datasetName, indexingTechnique, searchMethod |
| `DifyDatasetDeleteQo` | 批量删除请求 | agentId, List&lt;String&gt; datasetIds |
| `AgentIdQo` | 查询智能体绑定知识库请求 | List&lt;Integer&gt; agentIds |

#### 3.2 数据传输对象 (DTO)

| 类名 | 用途 | 字段 |
|------|------|------|
| `DifyAgentKnowledgeDto` | 智能体知识库列表（顶层） | agentId, List&lt;DifyAgentKnowledgeItem&gt; knowledgeList |
| `DifyAgentKnowledgeItem` | 知识库绑定项 | agentId, datasetId, datasetName, indexingTechnique, searchMethod |
| `DifyAgentKnowledgePageDto` | 分页响应总对象 | List&lt;DifyAgentKnowledgeItemDto&gt; rows, Integer total |
| `DifyAgentKnowledgeItemDto` | 知识库列表项 | id, name, description, indexingTechnique, searchMethod, authorName, updatedTime |

#### 3.3 Dify API 对象

| 类名 | 用途 |
|------|------|
| `DifyRetrieveRequest` | Dify 检索请求 (query: String) |
| `DifyRetrieveResponse` | Dify 检索响应 (query, records) |
| `DifyDatasetResponse` | Dify 知识库列表响应 (data, total, page, limit) |

---

### 步骤 4: 创建 Dify API 调用服务

**目标**: 实现调用 Dify 对外接口

创建 `DifyApiService.java`，包含：

#### 4.1 获取知识库列表
```java
DifyDatasetResponse getDatasetList(String keyword, Integer page, Integer limit)
```
- 调用: `GET /v1/datasets?page={page}&limit={limit}&keyword={keyword}&include_all=true`
- Header: `Authorization: Bearer {token}`

#### 4.2 检索知识片段
```java
DifyRetrieveResponse retrieveFromDataset(String datasetId, String query)
```
- 调用: `POST /v1/datasets/{dataset_id}/retrieve`
- Body: `{"query": "增强后的用户问题"}`
- Header: `Authorization: Bearer {token}`

---

### 步骤 5: 创建知识库业务服务

**目标**: 实现智能体与 Dify 知识库的绑定管理

创建 `DifyAgentKnowledgeService.java` 及实现类，包含：

| 方法名 | 说明 |
|--------|------|
| `getDatasetPage(DifyPageQo)` | 分页获取 Dify 知识库列表 |
| `addKnowledge(DifyAgentKnowledgeQo)` | 添加知识库与智能体绑定 |
| `deleteKnowledge(DifyDatasetDeleteQo)` | 批量删除知识库绑定 |
| `getByAgent(List&lt;Integer&gt; agentIds)` | 查询智能体绑定的知识库列表（支持多个智能体ID） |

---

### 步骤 6: 创建知识库控制器

**目标**: 提供 RESTful API 接口

创建 `DifyDatasetController.java`，包含 4 个接口：

| 方法 | 路径 | 入参 | 返回类型 | 说明 |
|------|------|------|---------|------|
| POST | `/dify/dataset/page` | DifyPageQo | Result&lt;DifyAgentKnowledgePageDto&gt; | 获取知识库列表(分页) |
| POST | `/dify/dataset/insert` | DifyAgentKnowledgeQo | Result&lt;Boolean&gt; | 添加知识库 |
| POST | `/dify/dataset/delete` | DifyDatasetDeleteQo | Result&lt;Boolean&gt; | 批量删除知识库 |
| POST | `/dify/dataset/getByAgent` | AgentIdQo | Result&lt;List&lt;DifyAgentKnowledgeDto&gt;&gt; | 查询智能体绑定的知识库列表 |

---

### 步骤 7: 修改 EvidenceRecallNode 支持 Dify 检索

**目标**: 在证据召回节点中增加 Dify 知识库检索功能

修改 `EvidenceRecallNode.java`：
- 注入 `DifyAgentKnowledgeService`
- 在 `retrieveDocuments` 方法中增加 Dify 知识库检索逻辑
- 获取智能体绑定的 Dify 知识库 ID 列表
- 循环调用 Dify 检索接口并合并结果

**检索逻辑**:
```java
// 获取智能体绑定的 Dify 知识库列表
List<AgentDatasetBinding> difyBindings = difyKnowledgeService.getDatasetsByAgentId(agentId);

// 循环调用 Dify 检索接口并合并结果
List<String> difyContents = new ArrayList<>();
for (AgentDatasetBinding binding : difyBindings) {
    DifyRetrieveResponse response = difyApiService.retrieveFromDataset(
        binding.getDatasetId(), query
    );
    // 提取 segment.content 存入 difyContents
}

// 构建 Document 列表
List<Document> difyDocuments = difyContents.stream()
    .map(content -> new Document(content))
    .collect(Collectors.toList());
```

---

## 关键文件清单

| 类型 | 文件路径 |
|------|---------|
| 配置 | `properties/DifyProperties.java` |
| 实体 | `entity/AgentDatasetBinding.java` |
| Mapper | `mapper/AgentDatasetBindingMapper.java` |
| QO | `qo/dify/DifyPageQo.java` |
| QO | `qo/dify/DifyAgentKnowledgeQo.java` |
| QO | `qo/dify/DifyDatasetDeleteQo.java` |
| QO | `qo/dify/AgentIdQo.java` |
| DTO | `dto/dify/DifyAgentKnowledgeDto.java` |
| DTO | `dto/dify/DifyAgentKnowledgePageDto.java` |
| DTO | `dto/dify/DifyAgentKnowledgeItemDto.java` |
| DTO | `dto/dify/DifyRetrieveRequest.java` |
| DTO | `dto/dify/DifyRetrieveResponse.java` |
| DTO | `dto/dify/DifyDatasetResponse.java` |
| 服务 | `service/dify/DifyAgentKnowledgeService.java` |
| 服务 | `service/dify/impl/DifyAgentKnowledgeServiceImpl.java` |
| 服务 | `service/dify/DifyApiService.java` |
| 服务 | `service/dify/impl/DifyApiServiceImpl.java` |
| 控制器 | `controller/DifyDatasetController.java` |
| 工作流节点 | `workflow/node/EvidenceRecallNode.java` |
| 配置文件 | `application.yml` |

---

## 验证方式

| 验证项 | 说明 |
|--------|------|
| 接口测试 | 测试 4 个 API 接口功能 |
| 接口1测试 | 分页获取知识库列表，验证索引方式、检索方式转中文 |
| 接口2测试 | 添加知识库绑定，验证数据库记录 |
| 接口3测试 | 批量删除知识库，验证数据库删除 |
| 接口4测试 | 查询智能体绑定的知识库列表（支持多个智能体ID） |
| 集成测试 | 创建智能体绑定 Dify 知识库，运行 Graph 分析流程验证检索结果 |
| 数据库验证 | 检查 `agent_dataset_binding` 表数据 |

---

## 注意事项

1. **驼峰命名规范**: DTO 和 QO 类使用驼峰命名（如 `indexingTechnique`、`searchMethod`、`authorName`）
2. **Dify API Token**: 应从配置文件读取，便于环境切换
3. **检索结果处理**: 提取 `records[].segment.content` 转换为 Spring AI `Document` 对象
4. **枚举转换**: 索引方式和检索方式需转换为中文显示
5. **批量支持**: 删除接口支持批量删除，`getByAgent` 支持多个智能体ID查询
6. **字段映射**: 数据库表字段使用下划线命名，实体类使用驼峰命名
7. **Result 类**: 使用已存在的 `com.nrec.base.common.model.Result` 类
   - `buildSuccess(T data)` - 成功返回数据
   - `buildSuccess(T data, String msg)` - 成功返回数据和消息
   - `buildFailed()` - 失败返回
   - `buildFailed(String msg)` - 失败返回消息

---

## 索引方式转换

| Dify 原始值 | 中文显示 |
|-------------|---------|
| high_quality | 高质量 |
| economy | 经济 |

---

## 检索方式转换

| Dify 原始值 | indexing_technique | 中文显示 |
|-------------|-------------------|---------|
| hybrid_search | high_quality | 混合检索 |
| hybrid_search | economy | 倒排索引 |
| full_text_search | - | 全文检索 |
| semantic_search | - | 向量检索 |

---

## Dify API 示例

### 1. 获取知识库列表

**请求**:
```http
GET http://172.18.0.9:4008/v1/datasets?page=1&limit=10&keyword=&include_all=true
Authorization: Bearer dataset-9Jj1gUdI20pf4nU1ilmQlcGg
```

**响应**:
```json
{
  "data": [
    {
      "id": "b76c6f08-50df-4305-82d2-8e31303dc236",
      "name": "测试知识库",
      "description": "测试知识库",
      "indexing_technique": "high_quality",
      "author_name": "nrec",
      "updated_at": 1770705854,
      "retrieval_model_dict": {
        "search_method": "semantic_search"
      }
    }
  ],
  "total": 6
}
```

---

### 2. 检索知识片段

**请求**:
```http
POST http://172.18.0.9:4008/v1/datasets/{dataset_id}/retrieve
Authorization: Bearer dataset-9Jj1gUdI20pf4nU1ilmQlcGg
Content-Type: application/json

{
  "query": "组件函数"
}
```

**响应**:
```json
{
  "query": {"content": "组件函数"},
  "records": [
    {
      "segment": {
        "id": "7797f78d-e463-4cd6-b110-09b391b52003",
        "content": "//import '@/webConfigure/path/method' 调用低代码平台组件函数...",
        "document": {
          "id": "f9e3b7e3-20c2-489a-9977-2cd618ebeabe",
          "name": "组件函数和db模块.txt"
        }
      },
      "score": 0.4380074665625974
    }
  ]
}
```

---

## 接口详细说明

### 1. 获取知识库列表(分页)

```
POST url:/dify/dataset/page
入参：DifyPageQo
返回类型：Result<DifyAgentKnowledgePageDto>

DifyPageQo:
{
    "keyword" : "",        // 关键词搜索
    "page" : 1,            // 当前页码
    "limit" : 10           // 每页大小
}

DifyAgentKnowledgePageDto:
{
    "rows" : [             // 知识库列表
        {
            "id" : "",     // 知识库id
            "name" : "",   // 知识库名称
            "description" : "",  // 知识库描述
            "indexingTechnique" : "",  // 索引方式(中文)
            "searchMethod" : "",      // 检索方式(中文)
            "authorName" : "",        // 创建人
            "updatedTime" : 1770705854 // 更新时间
        }
    ],
    "total" : 10           // 总数
}
```

---

### 2. 添加知识库

```
POST url:/dify/dataset/insert
入参：DifyAgentKnowledgeQo
返回类型: Result<Boolean>

DifyAgentKnowledgeQo:
{
    "agentId" : 1,                  // 智能体id
    "datasetId" : "",               // 知识库id
    "datasetName" : "",             // 知识库名称
    "indexingTechnique" : "",       // 索引方式
    "searchMethod" : ""             // 检索方式
}
```

---

### 3. 批量删除知识库

```
POST url:/dify/dataset/delete
入参：DifyDatasetDeleteQo
返回类型：Result<Boolean>

DifyDatasetDeleteQo:
{
    "agentId" : 1,                                        // 智能体id
    "datasetIds" : ["b76c6f08-50df-4305-82d2-8e31303dc236", "id2"]  // 知识库id列表
}
```

---

### 4. 查询指定智能体绑定的知识库列表

```
POST url:/dify/dataset/getByAgent
入参：AgentIdQo
返回类型：Result<List<DifyAgentKnowledgeDto>>

AgentIdQo:
{
    "agentIds" : [1]   // 智能体id列表
}

DifyAgentKnowledgeDto:
{
    "agentId" : 1,     // 智能体id
    "knowledgeList" : [ // 知识列表
        {
            "agentId" : 1,             // 智能体id
            "datasetId" : "",          // 知识库id
            "datasetName" : "",        // 知识库名称
            "indexingTechnique" : "",  // 索引方式
            "searchMethod" : ""        // 检索方式
        }
    ]
}
```

---

### 5. 批量检索逻辑 (未创建API接口)

在 `EvidenceRecallNode` 中实现：
1. 获取智能体绑定的所有 Dify 知识库 ID 列表
2. 循环调用 Dify 检索接口：`http://172.18.0.9:4008/v1/datasets/{dataset_id}/retrieve`
3. 合并所有知识库的检索结果
4. 返回统一的 Document 列表
