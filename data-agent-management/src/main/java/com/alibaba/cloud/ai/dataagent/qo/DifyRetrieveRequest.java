
package com.alibaba.cloud.ai.dataagent.qo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dify 知识库检索请求
 *
 * @author shihb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyRetrieveRequest {

	/**
	 * 检索查询内容
	 */
	@NotBlank(message = "查询内容不能为空")
	private String query;

}
