
package com.alibaba.cloud.ai.dataagent.qo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dify 知识库分页查询请求
 *
 * @author shihb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyPageQueryQo {

	/**
	 * 关键词搜索
	 */
	private String keyword = "";

	/**
	 * 当前页码
	 */
	@NotNull(message = "页码不能为空")
	@Min(value = 1, message = "页码不能小于1")
	private Integer page = 1;

	/**
	 * 每页大小
	 */
	@NotNull(message = "每页大小不能为空")
	@Min(value = 1, message = "每页大小不能小于1")
	private Integer limit = 10;

}
