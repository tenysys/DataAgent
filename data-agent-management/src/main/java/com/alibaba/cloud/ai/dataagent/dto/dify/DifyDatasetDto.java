
package com.alibaba.cloud.ai.dataagent.dto.dify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dify 知识库列表项
 *
 * @author shihb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyDatasetDto {

	/**
	 * 知识库ID
	 */
	private String id;

	/**
	 * 知识库名称
	 */
	private String name;

	/**
	 * 知识库描述
	 */
	private String description;

	/**
	 * 索引方式（中文显示）
	 */
	private String indexingTechnique;

	/**
	 * 检索方式（中文显示）
	 */
	private String searchMethod;

	/**
	 * 创建人
	 */
	private String authorName;

	/**
	 * 更新时间（时间戳）
	 */
	private Long updatedTime;

}
