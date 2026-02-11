
package com.alibaba.cloud.ai.dataagent.qo;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 智能体ID列表查询请求
 *
 * @author shihb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentIdsQueryQo {

	/**
	 * 智能体ID列表
	 */
	@NotEmpty(message = "智能体ID列表不能为空")
	private List<Integer> agentIds;

}
