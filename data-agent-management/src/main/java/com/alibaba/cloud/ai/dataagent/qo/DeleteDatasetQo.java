
package com.alibaba.cloud.ai.dataagent.qo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量删除知识库请求
 *
 * @author shihb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteDatasetQo {

	/**
	 * 智能体ID
	 */
	@NotNull(message = "智能体ID不能为空")
	private Integer agentId;

	/**
	 * 知识库ID列表
	 */
	private List<String> datasetIds;

}
