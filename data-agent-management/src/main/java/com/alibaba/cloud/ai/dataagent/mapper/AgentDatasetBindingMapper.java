
package com.alibaba.cloud.ai.dataagent.mapper;

import com.alibaba.cloud.ai.dataagent.entity.AgentDatasetBinding;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Agent Dataset Binding Mapper Interface
 * 智能体知识库绑定 Mapper 接口
 *
 * @author shihb
 */
@Mapper
public interface AgentDatasetBindingMapper {

	/**
	 * 根据智能体ID查询绑定的知识库列表
	 *
	 * @param agentId 智能体ID
	 * @return 知识库绑定列表
	 */
	@Select("""
			SELECT * FROM agent_dataset_binding WHERE agent_id = #{agentId}
			ORDER BY update_time DESC
			""")
	List<AgentDatasetBinding> selectByAgentId(@Param("agentId") Integer agentId);

	/**
	 * 根据智能体ID列表查询绑定的知识库列表（支持多个智能体）
	 *
	 * @param agentIds 智能体ID列表
	 * @return 知识库绑定列表
	 */
	@Select("""
			<script>
			SELECT * FROM agent_dataset_binding
			WHERE agent_id IN
			<foreach item="id" collection="agentIds" open="(" separator="," close=")">
			#{id}
			</foreach>
			ORDER BY update_time DESC
			</script>
			""")
	List<AgentDatasetBinding> selectByAgentIds(@Param("agentIds") List<Integer> agentIds);

	/**
	 * 根据智能体ID和知识库ID查询绑定记录
	 *
	 * @param agentId   智能体ID
	 * @param datasetId 知识库ID
	 * @return 绑定记录（不存在则返回null）
	 */
	@Select("""
			SELECT * FROM agent_dataset_binding
			WHERE agent_id = #{agentId} AND dataset_id = #{datasetId}
			""")
	AgentDatasetBinding selectByAgentIdAndDatasetId(@Param("agentId") Integer agentId,
			@Param("datasetId") String datasetId);

	/**
	 * 插入知识库绑定记录
	 *
	 * @param binding 绑定实体
	 * @return 插入的行数
	 */
	@Insert("""
			INSERT INTO agent_dataset_binding
			(agent_id, dataset_id, dataset_name, indexing_technique, search_method, update_time)
			VALUES (#{agentId}, #{datasetId}, #{datasetName}, #{indexingTechnique}, #{searchMethod}, NOW())
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	int insert(AgentDatasetBinding binding);

	/**
	 * 根据ID删除绑定记录
	 *
	 * @param id 绑定记录ID
	 * @return 删除的行数
	 */
	@Delete("""
			DELETE FROM agent_dataset_binding WHERE id = #{id}
			""")
	int deleteById(@Param("id") Integer id);

	/**
	 * 根据智能体ID删除所有绑定记录
	 *
	 * @param agentId 智能体ID
	 * @return 删除的行数
	 */
	@Delete("""
			DELETE FROM agent_dataset_binding WHERE agent_id = #{agentId}
			""")
	int deleteByAgentId(@Param("agentId") Integer agentId);

	/**
	 * 根据智能体ID和知识库ID列表批量删除绑定记录
	 *
	 * @param agentId    智能体ID
	 * @param datasetIds 知识库ID列表
	 * @return 删除的行数
	 */
	@Delete("""
			<script>
			DELETE FROM agent_dataset_binding
			WHERE agent_id = #{agentId} AND dataset_id IN
			<foreach item="id" collection="datasetIds" open="(" separator="," close=")">
			#{id}
			</foreach>
			</script>
			""")
	int deleteByAgentIdAndDatasetIds(@Param("agentId") Integer agentId,
			@Param("datasetIds") List<String> datasetIds);

}
