
package com.alibaba.cloud.ai.dataagent.dto.dify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dify 知识库列表响应
 *
 * @author shihb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyDatasetResponse {

	/**
	 * 知识库列表
	 */
	private List<DifyDatasetData> data;

	/**
	 * 是否有更多数据
	 */
	private Boolean hasMore;

	/**
	 * 每页大小
	 */
	private Integer limit;

	/**
	 * 总数
	 */
	private Integer total;

	/**
	 * 当前页码
	 */
	private Integer page;

	/**
	 * 知识库数据
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DifyDatasetData {

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
		 * 提供商
		 */
		private String provider;

		/**
		 * 索引方式
		 */
		private String indexingTechnique;

		/**
		 * 创建人名称
		 */
		private String authorName;

		/**
		 * 更新时间戳
		 */
		private Long updatedAt;

		/**
		 * 检索配置
		 */
		private RetrievalModelDict retrievalModelDict;

	}

	/**
	 * 检索配置
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RetrievalModelDict {

		/**
		 * 检索方式
		 */
		private String searchMethod;

	}

}
