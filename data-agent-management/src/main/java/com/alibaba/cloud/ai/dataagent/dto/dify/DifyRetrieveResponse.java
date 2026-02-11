
package com.alibaba.cloud.ai.dataagent.dto.dify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dify 知识库检索响应
 *
 * @author shihb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyRetrieveResponse {

	/**
	 * 查询对象
	 */
	private QueryInfo query;

	/**
	 * 检索记录列表
	 */
	private List<RetrieveRecord> records;

	/**
	 * 查询信息
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class QueryInfo {

		/**
		 * 查询内容
		 */
		private String content;

	}

	/**
	 * 检索记录
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RetrieveRecord {

		/**
		 * 文档片段
		 */
		private Segment segment;

		/**
		 * 相似度分数
		 */
		private Double score;

		/**
		 * 文档片段
		 */
		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		public static class Segment {

			/**
			 * 片段ID
			 */
			private String id;

			/**
			 * 片段位置
			 */
			private Integer position;

			/**
			 * 文档ID
			 */
			private String documentId;

			/**
			 * 片段内容
			 */
			private String content;

			/**
			 * 签名内容
			 */
			private String signContent;

			/**
			 * 字数
			 */
			private Integer wordCount;

			/**
			 * Token数
			 */
			private Integer tokens;

			/**
			 * 关键词
			 */
			private Object keywords;

			/**
			 * 索引节点ID
			 */
			private String indexNodeId;

			/**
			 * 索引节点哈希
			 */
			private String indexNodeHash;

			/**
			 * 命中次数
			 */
			private Integer hitCount;

			/**
			 * 是否启用
			 */
			private Boolean enabled;

			/**
			 * 状态
			 */
			private String status;

			/**
			 * 文档信息
			 */
			private DocumentInfo document;

		}

		/**
		 * 文档信息
		 */
		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		public static class DocumentInfo {

			/**
			 * 文档ID
			 */
			private String id;

			/**
			 * 数据源类型
			 */
			private String dataSourceType;

			/**
			 * 文档名称
			 */
			private String name;

			/**
			 * 文档类型
			 */
			private String docType;

			/**
			 * 文档元数据
			 */
			private Object docMetadata;

		}

	}

}
