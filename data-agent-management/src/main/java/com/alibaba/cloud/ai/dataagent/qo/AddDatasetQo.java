
package com.alibaba.cloud.ai.dataagent.qo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 添加知识库请求
 *
 * @author shihb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDatasetQo {

	/**
	 * 智能体ID
	 */
	@NotNull(message = "智能体ID不能为空")
	private Integer agentId;

	/**
	 * 知识库ID
	 */
	@NotBlank(message = "知识库ID不能为空")
	private String datasetId;

	/**
	 * 知识库名称
	 */
	@NotBlank(message = "知识库名称不能为空")
	private String datasetName;

	/**
	 * 索引方式
	 */
	private String indexingTechnique;

	/**
	 * 检索方式
	 */
	private String searchMethod;

}
