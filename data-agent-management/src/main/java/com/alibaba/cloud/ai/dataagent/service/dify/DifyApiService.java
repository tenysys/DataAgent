
package com.alibaba.cloud.ai.dataagent.service.dify;

import com.alibaba.cloud.ai.dataagent.dto.dify.DifyDatasetResponse;
import com.alibaba.cloud.ai.dataagent.dto.dify.DifyRetrieveResponse;
import com.alibaba.cloud.ai.dataagent.qo.DifyPageQueryQo;
import com.alibaba.cloud.ai.dataagent.qo.DifyRetrieveRequest;

/**
 * Dify API 调用服务接口
 *
 * @author shihb
 */
public interface DifyApiService {

	/**
	 * 获取 Dify 知识库列表
	 *
	 * @param query 查询参数
	 * @return 知识库列表响应
	 * @throws RuntimeException 调用失败时抛出异常
	 */
	DifyDatasetResponse getDatasetList(DifyPageQueryQo query);

	/**
	 * 从指定知识库中检索相关片段
	 *
	 * @param datasetId 知识库ID
	 * @param request   检索请求
	 * @return 检索响应
	 * @throws RuntimeException 调用失败时抛出异常
	 */
	DifyRetrieveResponse retrieveFromDataset(String datasetId, DifyRetrieveRequest request);

}
