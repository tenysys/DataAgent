
package com.alibaba.cloud.ai.dataagent.service.dify.impl;

import com.alibaba.cloud.ai.dataagent.dto.dify.DifyDatasetResponse;
import com.alibaba.cloud.ai.dataagent.dto.dify.DifyRetrieveResponse;
import com.alibaba.cloud.ai.dataagent.properties.DifyProperties;
import com.alibaba.cloud.ai.dataagent.qo.DifyPageQueryQo;
import com.alibaba.cloud.ai.dataagent.qo.DifyRetrieveRequest;
import com.alibaba.cloud.ai.dataagent.service.dify.DifyApiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Dify API 调用服务实现
 *
 * @author shihb
 */
@Slf4j
@Service
@AllArgsConstructor
public class DifyApiServiceImpl implements DifyApiService {

	private final WebClient.Builder webClientBuilder;

	private final DifyProperties difyProperties;

	private static final String BEARER_PREFIX = "Bearer ";

	@Override
	public DifyDatasetResponse getDatasetList(DifyPageQueryQo query) {
		try {
			DifyDatasetResponse response = webClientBuilder.build().get().uri(uriBuilder -> uriBuilder
					.path(difyProperties.getApiUrl())
					.path("/v1/datasets")
					.queryParam("page", query.getPage())
					.queryParam("limit", query.getLimit())
					.queryParam("keyword", query.getKeyword())
					.queryParam("include_all", true)
					.build()).header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + difyProperties.getApiToken())
					.retrieve().bodyToMono(DifyDatasetResponse.class).block();

			log.info("成功获取 Dify 知识库列表，共 {} 条", response != null ? response.getTotal() : 0);
			return response;
		}
		catch (WebClientResponseException e) {
			log.error("调用 Dify API 获取知识库列表失败，状态码: {}, 响应: {}", e.getStatusCode(),
					e.getResponseBodyAsString());
			throw new RuntimeException("获取 Dify 知识库列表失败: " + e.getMessage(), e);
		}
		catch (Exception e) {
			log.error("调用 Dify API 获取知识库列表异常", e);
			throw new RuntimeException("获取 Dify 知识库列表异常: " + e.getMessage(), e);
		}
	}

	@Override
	public DifyRetrieveResponse retrieveFromDataset(String datasetId, DifyRetrieveRequest request) {
		try {
			DifyRetrieveResponse response = webClientBuilder.build().post().uri(difyProperties.getApiUrl()
					+ "/v1/datasets/" + datasetId + "/retrieve")
					.header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + difyProperties.getApiToken())
					.contentType(MediaType.APPLICATION_JSON).bodyValue(request).retrieve()
					.bodyToMono(DifyRetrieveResponse.class).block();

			int recordCount = response != null && response.getRecords() != null ? response.getRecords().size() : 0;
			log.info("成功从知识库 {} 检索到 {} 条相关片段", datasetId, recordCount);
			return response;
		}
		catch (WebClientResponseException e) {
			log.error("调用 Dify API 检索知识库失败，状态码: {}, 响应: {}", e.getStatusCode(),
					e.getResponseBodyAsString());
			throw new RuntimeException("检索 Dify 知识库失败: " + e.getMessage(), e);
		}
		catch (Exception e) {
			log.error("调用 Dify API 检索知识库异常", e);
			throw new RuntimeException("检索 Dify 知识库异常: " + e.getMessage(), e);
		}
	}

}
