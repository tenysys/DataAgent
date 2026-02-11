
package com.alibaba.cloud.ai.dataagent.dto.dify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识库绑定项
 *
 * @author shihb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasetItemDto {

	/**
	 * 智能体ID
	 */
	private Integer agentId;

	/**
	 * 知识库ID
	 */
	private String datasetId;

	/**
	 * 知识库名称
	 */
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
