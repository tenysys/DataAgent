
package com.alibaba.cloud.ai.dataagent.dto.dify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 智能体知识库绑定列表响应
 *
 * @author shihb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentDatasetDto {

	/**
	 * 智能体ID
	 */
	private Integer agentId;

	/**
	 * 知识库列表
	 */
	private List<DatasetItemDto> knowledgeList;

}
