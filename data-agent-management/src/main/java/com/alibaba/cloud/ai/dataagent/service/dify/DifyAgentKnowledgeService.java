
package com.alibaba.cloud.ai.dataagent.service.dify;

import com.alibaba.cloud.ai.dataagent.dto.dify.AgentDatasetDto;
import com.alibaba.cloud.ai.dataagent.dto.dify.DifyDatasetDto;
import com.alibaba.cloud.ai.dataagent.qo.AddDatasetQo;
import com.alibaba.cloud.ai.dataagent.qo.AgentIdsQueryQo;
import com.alibaba.cloud.ai.dataagent.qo.DifyPageQueryQo;

import java.util.List;

/**
 * Dify 知识库绑定管理服务接口
 *
 * @author shihb
 */
public interface DifyAgentKnowledgeService {

	/**
	 * 分页获取 Dify 知识库列表
	 *
	 * @param query 查询参数
	 * @return Dify 知识库列表
	 * @throws RuntimeException 调用失败时抛出异常
	 */
	List<DifyDatasetDto> getDatasetPage(DifyPageQueryQo query);

	/**
	 * 获取知识库总数
	 *
	 * @param query 查询参数
	 * @return 总数
	 * @throws RuntimeException 调用失败时抛出异常
	 */
	Integer getDatasetTotal(DifyPageQueryQo query);

	/**
	 * 添加知识库与智能体绑定
	 *
	 * @param request 添加知识库请求
	 * @return true 成功，false 失败
	 * @throws RuntimeException 调用失败时抛出异常
	 */
	Boolean addKnowledge(AddDatasetQo request);

	/**
	 * 批量删除知识库绑定
	 *
	 * @param agentIdQueryQo 删除知识库请求
	 * @return true 成功，false 失败
	 * @throws RuntimeException 调用失败时抛出异常
	 */
	Boolean deleteKnowledge(String agentIdQueryQo);

	/**
	 * 查询指定智能体绑定的知识库列表
	 *
	 * @param query 智能体ID列表查询请求
	 * @return 知识库绑定列表
	 * @throws RuntimeException 调用失败时抛出异常
	 */
	List<AgentDatasetDto> getByAgent(AgentIdsQueryQo query);

}
