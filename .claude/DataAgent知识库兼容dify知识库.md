**需求**
1. 将现有的智能体知识和业务知识(同义词库)替换为dify知识库，需要提供dify知识库列表
2. 用户可以在知识库列表中添加选好的知识库(和智能体绑定)，点击知识库详情跳转对应的dify知识库详情页面
3. 增加知识库来源方式dify，在EvidenceRecallNode中增加检索dify知识库的情况，根据增强问题检索知识片段，对检索到的片段内容进行处理，处理方式和智能体知识文档一致


**数据库表**
智能体绑定知识库的存数数据表

| 字段名                | 类型           | 说明      |
| ------------------ | ------------ | ------- |
| id                 | int(11)      | 主键id，自增 |
| agent_id           | int(11)      | 智能体id   |
| dataset_id         | varchar(64)  | 知识库id   |
| dataset_name       | varchar(256) | 知识库名称   |
| indexing_technique | varchar(64)  | 索引方式    |
| search_method      | varchar(64)  | 检索方式    |

### 接口
#### 1.获取知识库列表(分页)
```
post url:/dify/dataset/page
入参：DifyPageQo
返回类型：Result<DifyAgentKnowledgeDto>

DifyPageQo:
{
	//关键词搜索
	"keyword" : "",
	//当前页码
	"page" : 1,
	//每页大小
	"limit" : 10
}

DifyAgentKnowledgePageDto:
{
	"rows" : [
		{
			//知识库id
			"id" : "",
			//知识库名称
			"name" : "",
			//知识库描述
			"description" : "",
			//索引方式
			"indexingTechnique" : "",
			//检索方式
			"searchMethod" : "",
			//创建人
			"authorName" : "",
			//更新时间
			"updatedTime" : 1770705854
		}
	],
	"total" : 10
}
```
**注意**
1. 需要调用dify的接口来获取知识库列表
```
get url:http://172.18.0.9:4008/v1/datasets
请求头: Authorization = "Bearer dataset-9Jj1gUdI20pf4nU1ilmQlcGg"
请求参数: page(当前页码),limit(分页大小),keyword(关键词搜索),include_all(是否包含所有数据集,固定为true)
返回结构:
{
  "data": [
    {
      "id": "b76c6f08-50df-4305-82d2-8e31303dc236",
      "name": "测试知识库",
      "description": "测试知识库",
      "provider": "vendor",
      "permission": "all_team_members",
      "data_source_type": "upload_file",
      "indexing_technique": "high_quality",
      "app_count": 0,
      "document_count": 1,
      "word_count": 13649,
      "created_by": "5135919c-025a-4367-beb6-140f6e4abd07",
      "author_name": "nrec",
      "created_at": 1770701796,
      "updated_by": "5135919c-025a-4367-beb6-140f6e4abd07",
      "updated_at": 1770705854,
      "embedding_model": "bge_m3",
      "embedding_model_provider": "langgenius/openai_api_compatible/openai_api_compatible",
      "embedding_available": true,
      "retrieval_model_dict": {
        "search_method": "semantic_search",
        "reranking_enable": true,
        "reranking_mode": null,
        "reranking_model": {
          "reranking_provider_name": "langgenius/openai_api_compatible/openai_api_compatible",
          "reranking_model_name": "bge_rerank_v2_m3"
        },
        "weights": null,
        "top_k": 5,
        "score_threshold_enabled": true,
        "score_threshold": null
      },
      "summary_index_setting": {
        "enable": null,
        "model_name": null,
        "model_provider_name": null,
        "summary_prompt": null
      },
      "tags": [],
      "doc_form": "text_model",
      "external_knowledge_info": {
        "external_knowledge_id": null,
        "external_knowledge_api_id": null,
        "external_knowledge_api_name": null,
        "external_knowledge_api_endpoint": null
      },
      "external_retrieval_model": {
        "top_k": 5,
        "score_threshold": null,
        "score_threshold_enabled": true
      },
      "doc_metadata": [],
      "built_in_field_enabled": false,
      "pipeline_id": null,
      "runtime_mode": "general",
      "chunk_structure": null,
      "icon_info": {
        "icon_type": "emoji",
        "icon": "",
        "icon_background": "",
        "icon_url": null
      },
      "is_published": false,
      "total_documents": 1,
      "total_available_documents": 1,
      "enable_api": true,
      "is_multimodal": false
    }
  ],
  "has_more": false,
  "limit": 20,
  "total": 6,
  "page": 1
}
```
2. indexing_technique索引方式在查询结果中需要转换为中文
   high_quality -> 高质量
   economy -> 经济
3. search_method检索方式在查询结果中需要转换为中文
   hybrid_search -> indexing_technique=high_quality时是"混合检索"，indexing_technique=economy时是"倒排索引"
   full_text_search -> 全文检索
   semantic_search -> 向量检索

#### 2.添加知识库

```
post url:/dify/dataset/insert
入参：DifyAgentKnowledgeQo
返回类型: Result<Boolean>

DifyAgentKnowledgeQo:
{
	//智能体id
	"agentId" : 1,
	//知识库id
	"datasetId" : "",
	//知识库名称
	"datasetName" : "",
	//索引方式
	"indexingTechnique" : "",
	//检索方式
	"searchMethod" : ""
}

```
#### 3.批量删除知识库
```  
post url:/dify/dataset/delete  
入参：DifyDatasetDeleteQo  
返回类型：Result<Boolean>  
  
DifyDatasetDeleteQo:  
{  
    //智能体id  
    "agentId" : 1,    //知识库id列表（批量删除）  
    "datasetIds" : ["b76c6f08-50df-4305-82d2-8e31303dc236", "another-dataset-id"]
}  
```  

#### 4.查询指定智能体绑定的知识库列表
```  
post url:/dify/dataset/getByAgent  
入参：AgentIdQo
返回类型：Result<List<DifyAgentKnowledgeDto>>  
  
AgentIdQo:  
{  
    //智能体id  
    "agentIds" : [1]
}
  
DifyAgentKnowledgeDto:  
{
	//智能体id
	"agentId" : 1,
	//知识列表
	"knowledgeList" : {  
		//智能体id
		"agentId" : 1,
		//知识库id
		"datasetId" : "",
		//知识库名称
		"datasetName" : "",
		//索引方式
		"indexingTechnique" : "",
		//检索方式
		"searchMethod" : ""
	} 
} 
```  
**说明**：从agent_dataset_binding表中查询指定智能体绑定的所有知识库列表


#### 4.批量检索逻辑
**说明**：在EvidenceRecallNode中实现，不需要创建新的API接口
- 获取智能体绑定的所有Dify知识库ID列表
- 循环调用Dify检索接口：`http://172.18.0.9:4008/v1/datasets/{dataset_id}/retrieve`
- 合并所有知识库的检索结果
- 返回统一的Document列表

**Dify检索接口调用示例**：
```  
post url:http://172.18.0.9:4008/v1/datasets/{dataset_id}/retrieve
请求头: Authorization = "Bearer dataset-9Jj1gUdI20pf4nU1ilmQlcGg"
路径参数: dataset_id(知识库id)
请求体参数: query(增强后的用户问题)
返回结构: 
{
  "query": {
    "content": "组件函数"
  },
  "records": [
    {
      "segment": {
        "id": "7797f78d-e463-4cd6-b110-09b391b52003",
        "position": 3,
        "document_id": "f9e3b7e3-20c2-489a-9977-2cd618ebeabe",
        "content": "//import '@/webConfigure/path/method‘ 调用低代码平台组件函数,其中path是函数路径，method是函数名称\n// 调用低代码平台生成饼图组件的函数,其中函数路径是echarts,函数名称是PieChart,pieChart为生成饼图组件函数的赋值变量名称\nimport '@/webConfigure/echarts/PieChart' as pieChart;\nimport log;\nimport json;\n// data为生成饼图组件的示例数据\nlet data = [{name:\"shi\",ball:98,phone:96,watch:94},{name:\"liu\",ball:88,phone:86,watch:92},{name:\"luo\",ball:85,phone:99,watch:91},{name:\"qian\",ball:76,phone:88,watch:94}];\n// 示例:设置xAxis为name,分类字段为name,yAxis为ball,值字段为ball\nlet map1 = {xAxis:\"name\",yAxis:\"ball\",data:data};\n// param为空值或者null,组件参数采用默认参数值\nlet param1 = [];\n// 其中/* */中间是参数解释不用于组件函数参数生成\nlet param2 = {echartsGenericComponent_Basic:{sort:false/*饼图不进行排序*/,clockwise:true/*饼图按照顺时针显示*/,percentDisplay:true/*饼图显示百分比*/},echartsGenericComponent_PieLabel:{label_show:false/*饼图不显示标签*/,label_formatterName:true/*label_show:true,饼图标签显示名称*/,label_lineFeed:false/*label_show:true,饼图标签不换行*/,label_formatterNum:false/*label_show:true,饼图标签不显示数值*/,label_formatterPercent:false/*label_show:true,饼图标签不显示百分比*/,avoidLabelOverlap:true/*label_show:true,饼图标签允许重叠*/,label_fontSize:12/*label_show:true,饼图标签字体大小为12*/,label_subFontSize:12/*label_show:true,饼图副标签字体大小为12*/,label_position:\"outer\"/*label_show:true,饼图标签在外部显示,内部显示inner,中心显示center*/},echartsGenericComponent_Tooltips:{show:true/*饼图显示提示框*/,textStyle_fontSize:14/*show:true,设置提示文字大小为14*/}}\n// 要求饼图显示标签,标签可以换行,副标签字体大小为16,提示框字体大小为12\nlet param3 = {echartsGenericComponent_PieLabel:{label_show:true,label_lineFeed:true,label_subFontSize:16},echartsGenericComponent_Tooltips:{textStyle_fontSize:12}}\n// 要求饼图进行排序,按照逆时针显示,显示在饼图内部显示标签,不显示提示框\nlet param4 = {echartsGenericComponent_Basic:{sort:true,clockwise:false},echartsGenericComponent_PieLabel:{label_show:true,label_position:\"inner\"},echartsGenericComponent_Tooltips:{show:false}}\nlet result1 = pieChart(param1,map1);\n// 采用log.info输出result1,因为log.info(str)中str是String类型,result1是JSONObject,采用json模块通过json.toJSONString(result1)将result1转换为json字符串类型\nlog.info(json.toJSONString(result1));\nlet result2 = pieChart(param2,map1);\nlog.info(json.toJSONString(result2));\nreturn {result1:result1,result2:result2};",
        "sign_content": "//import '@/webConfigure/path/method‘ 调用低代码平台组件函数,其中path是函数路径，method是函数名称\n// 调用低代码平台生成饼图组件的函数,其中函数路径是echarts,函数名称是PieChart,pieChart为生成饼图组件函数的赋值变量名称\nimport '@/webConfigure/echarts/PieChart' as pieChart;\nimport log;\nimport json;\n// data为生成饼图组件的示例数据\nlet data = [{name:\"shi\",ball:98,phone:96,watch:94},{name:\"liu\",ball:88,phone:86,watch:92},{name:\"luo\",ball:85,phone:99,watch:91},{name:\"qian\",ball:76,phone:88,watch:94}];\n// 示例:设置xAxis为name,分类字段为name,yAxis为ball,值字段为ball\nlet map1 = {xAxis:\"name\",yAxis:\"ball\",data:data};\n// param为空值或者null,组件参数采用默认参数值\nlet param1 = [];\n// 其中/* */中间是参数解释不用于组件函数参数生成\nlet param2 = {echartsGenericComponent_Basic:{sort:false/*饼图不进行排序*/,clockwise:true/*饼图按照顺时针显示*/,percentDisplay:true/*饼图显示百分比*/},echartsGenericComponent_PieLabel:{label_show:false/*饼图不显示标签*/,label_formatterName:true/*label_show:true,饼图标签显示名称*/,label_lineFeed:false/*label_show:true,饼图标签不换行*/,label_formatterNum:false/*label_show:true,饼图标签不显示数值*/,label_formatterPercent:false/*label_show:true,饼图标签不显示百分比*/,avoidLabelOverlap:true/*label_show:true,饼图标签允许重叠*/,label_fontSize:12/*label_show:true,饼图标签字体大小为12*/,label_subFontSize:12/*label_show:true,饼图副标签字体大小为12*/,label_position:\"outer\"/*label_show:true,饼图标签在外部显示,内部显示inner,中心显示center*/},echartsGenericComponent_Tooltips:{show:true/*饼图显示提示框*/,textStyle_fontSize:14/*show:true,设置提示文字大小为14*/}}\n// 要求饼图显示标签,标签可以换行,副标签字体大小为16,提示框字体大小为12\nlet param3 = {echartsGenericComponent_PieLabel:{label_show:true,label_lineFeed:true,label_subFontSize:16},echartsGenericComponent_Tooltips:{textStyle_fontSize:12}}\n// 要求饼图进行排序,按照逆时针显示,显示在饼图内部显示标签,不显示提示框\nlet param4 = {echartsGenericComponent_Basic:{sort:true,clockwise:false},echartsGenericComponent_PieLabel:{label_show:true,label_position:\"inner\"},echartsGenericComponent_Tooltips:{show:false}}\nlet result1 = pieChart(param1,map1);\n// 采用log.info输出result1,因为log.info(str)中str是String类型,result1是JSONObject,采用json模块通过json.toJSONString(result1)将result1转换为json字符串类型\nlog.info(json.toJSONString(result1));\nlet result2 = pieChart(param2,map1);\nlog.info(json.toJSONString(result2));\nreturn {result1:result1,result2:result2};",
        "answer": null,
        "word_count": 2083,
        "tokens": 1396,
        "keywords": null,
        "index_node_id": "736e16eb-8b9a-42aa-a8fe-a18490fc8fb9",
        "index_node_hash": "be7987cc9957d386b7ed36973662077ebf8cdfe6cf8afb2a934e0819767b0f65",
        "hit_count": 3,
        "enabled": true,
        "disabled_at": null,
        "disabled_by": null,
        "status": "completed",
        "created_by": "84a18b57-0a7e-415b-b8b8-464ffb51aa7b",
        "created_at": 1766714093,
        "indexing_at": 1766714092,
        "completed_at": 1766714094,
        "error": null,
        "stopped_at": null,
        "document": {
          "id": "f9e3b7e3-20c2-489a-9977-2cd618ebeabe",
          "data_source_type": "upload_file",
          "name": "组件函数和db模块.txt",
          "doc_type": null,
          "doc_metadata": null
        }
      },
      "child_chunks": null,
      "score": 0.4380074665625974,
      "tsne_position": null,
      "files": null,
      "summary": null
    }
  ]
}
```  

**注意**
1. 检索结果需要提取片段内容(records.segment.content)，根据List<String>构建List<Document>
2. 批量检索逻辑在EvidenceRecallNode中实现，循环调用dify检索接口并合并结果
3. 删除接口使用POST方法，入参为List<String>支持批量删除