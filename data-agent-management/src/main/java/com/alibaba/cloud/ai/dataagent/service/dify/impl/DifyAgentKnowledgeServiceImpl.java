
package com.alibaba.cloud.ai.dataagent.service.dify.impl;

import com.alibaba.cloud.ai.dataagent.dto.dify.AgentDatasetDto;
import com.alibaba.cloud.ai.dataagent.dto.dify.DatasetItemDto;
import com.alibaba.cloud.ai.dataagent.dto.dify.DifyDatasetDto;
import com.alibaba.cloud.ai.dataagent.dto.dify.DifyDatasetResponse;
import com.alibaba.cloud.ai.dataagent.entity.AgentDatasetBinding;
import com.alibaba.cloud.ai.dataagent.mapper.AgentDatasetBindingMapper;
import com.alibaba.cloud.ai.dataagent.qo.AddDatasetQo;
import com.alibaba.cloud.ai.dataagent.qo.AgentIdsQueryQo;
import com.alibaba.cloud.ai.dataagent.qo.DifyPageQueryQo;
import com.alibaba.cloud.ai.dataagent.service.dify.DifyAgentKnowledgeService;
import com.alibaba.cloud.ai.dataagent.service.dify.DifyApiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dify 知识库绑定管理服务实现
 *
 * @author shihb
 */
@Slf4j
@Service
@AllArgsConstructor
public class DifyAgentKnowledgeServiceImpl implements DifyAgentKnowledgeService {

	private final DifyApiService difyApiService;

	private final AgentDatasetBindingMapper agentDatasetBindingMapper;

	/**
	 * 索引方式转换映射
	 */
	private static final Map<String, String> INDEXING_TECHNIQUE_MAP = new HashMap<>();

	/**
	 * 检索方式转换映射
	 */
	private static final Map<String, String> SEARCH_METHOD_MAP = new HashMap<>();

	static {
		INDEXING_TECHNIQUE_MAP.put("high_quality", "高质量");
		INDEXING_TECHNIQUE_MAP.put("economy", "经济");

		SEARCH_METHOD_MAP.put("semantic_search", "向量检索");
		SEARCH_METHOD_MAP.put("full_text_search", "全文检索");
		SEARCH_METHOD_MAP.put("hybrid_search", "混合检索");
	}

	@Override
	public List<DifyDatasetDto> getDatasetPage(DifyPageQueryQo query) {
		DifyDatasetResponse response = difyApiService.getDatasetList(query);
		if (response == null || response.getData() == null) {
			return new ArrayList<>();
		}
		return response.getData().stream().map(this::convertToDifyDatasetDto).collect(Collectors.toList());
	}

	@Override
	public Integer getDatasetTotal(DifyPageQueryQo query) {
		DifyDatasetResponse response = difyApiService.getDatasetList(query);
		return response != null ? response.getTotal() : 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean addKnowledge(AddDatasetQo request) {
		// 检查是否已存在绑定
		AgentDatasetBinding existing = agentDatasetBindingMapper
				.selectByAgentIdAndDatasetId(request.getAgentId(), request.getDatasetId());
		if (existing != null) {
			log.warn("智能体 {} 已绑定知识库 {}", request.getAgentId(), request.getDatasetId());
			return true;
		}

		// 创建绑定记录
		AgentDatasetBinding binding = new AgentDatasetBinding();
		binding.setAgentId(request.getAgentId());
		binding.setDatasetId(request.getDatasetId());
		binding.setDatasetName(request.getDatasetName());
		binding.setIndexingTechnique(request.getIndexingTechnique());
		binding.setSearchMethod(request.getSearchMethod());

		int result = agentDatasetBindingMapper.insert(binding);
		log.info("成功为智能体 {} 绑定知识库 {}: {}", request.getAgentId(), request.getDatasetId(),
				request.getDatasetName());
		return result > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean deleteKnowledge(String agentIdQueryQo) {
		com.alibaba.cloud.ai.dataagent.qo.DeleteDatasetQo request = new com.alibaba.cloud.ai.dataagent.qo.DeleteDatasetQo();
		String[] parts = agentIdQueryQo.split(":");
		request.setAgentId(Integer.parseInt(parts[0]));
		List<String> datasetIds = new ArrayList<>();
		datasetIds.add(parts[1]);
		request.setDatasetIds(datasetIds);

		int result = agentDatasetBindingMapper.deleteByAgentIdAndDatasetIds(request.getAgentId(), request.getDatasetIds());
		log.info("成功删除智能体 {} 的知识库绑定: {}", request.getAgentId(), request.getDatasetIds());
		return result > 0;
	}

	@Override
	public List<AgentDatasetDto> getByAgent(AgentIdsQueryQo query) {
		List<AgentDatasetBinding> bindings = agentDatasetBindingMapper.selectByAgentIds(query.getAgentIds());
		if (bindings == null || bindings.isEmpty()) {
			return new ArrayList<>();
		}
		return bindings.stream().map(this::convertBindingToDifyDatasetDto).collect(Collectors.groupingBy(DatasetItemDto::getAgentId))
				.entrySet().stream().map(this::convertMapToAgentDatasetDto).collect(Collectors.toList());
	}

	/**
	 * 将 Dify API 返回的数据转换为 DTO
	 * @param data dify查询到的知识库数据
	 * @return 知识库数据
	 */
	private DifyDatasetDto convertToDifyDatasetDto(DifyDatasetResponse.DifyDatasetData data) {
		DifyDatasetDto dto = new DifyDatasetDto();
		dto.setId(data.getId());
		dto.setName(data.getName());
		dto.setDescription(data.getDescription());
		dto.setAuthorName(data.getAuthorName());
		dto.setUpdatedTime(data.getUpdatedAt());

		// 转换索引方式（英文 -> 中文）
		dto.setIndexingTechnique(INDEXING_TECHNIQUE_MAP.getOrDefault(data.getIndexingTechnique(),
				data.getIndexingTechnique()));

		// 转换检索方式（英文 -> 中文）
		String searchMethod = null;
		if (data.getRetrievalModelDict() != null && data.getRetrievalModelDict().getSearchMethod() != null) {
			searchMethod = SEARCH_METHOD_MAP.getOrDefault(data.getRetrievalModelDict().getSearchMethod(),
					data.getRetrievalModelDict().getSearchMethod());
		}
		dto.setSearchMethod("economy".equals(data.getIndexingTechnique())?"倒排索引":searchMethod);

		return dto;
	}

	/**
	 * 将数据库绑定记录转换为 DTO
	 * @param binding 绑定知识库数据
	 * @return 知识库数据
	 */
	private DatasetItemDto convertBindingToDifyDatasetDto(AgentDatasetBinding binding) {
		DatasetItemDto dto = new DatasetItemDto();
		dto.setAgentId(binding.getAgentId());
		dto.setDatasetId(binding.getDatasetId());
		dto.setDatasetName(binding.getDatasetName());

		// 转换索引方式（英文 -> 中文）
		dto.setIndexingTechnique(INDEXING_TECHNIQUE_MAP.getOrDefault(binding.getIndexingTechnique(),
				binding.getIndexingTechnique()));

		// 转换检索方式（英文 -> 中文）
		dto.setSearchMethod("economy".equals(binding.getIndexingTechnique())?"倒排索引"
				:SEARCH_METHOD_MAP.getOrDefault(binding.getSearchMethod(), binding.getSearchMethod()));
		dto.setIndexingTechnique(binding.getIndexingTechnique());
		dto.setSearchMethod(binding.getSearchMethod());
		return dto;
	}

	/**
	 * 将map转换为智能体知识库
	 * @param entry Map.Entry
	 * @return AgentDatasetDto
	 */
	private AgentDatasetDto convertMapToAgentDatasetDto(Map.Entry<Integer, List<DatasetItemDto>> entry) {
		AgentDatasetDto dto = new AgentDatasetDto();
		dto.setAgentId(entry.getKey());
		dto.setKnowledgeList(entry.getValue());
		return dto;
	}

}
