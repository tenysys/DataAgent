/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.dataagent.properties;

import com.alibaba.cloud.ai.dataagent.constant.Constant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dify 知识库相关配置属性。
 *
 * @author shihb
 */
@Getter
@Setter
@ConfigurationProperties(prefix = Constant.DIFY_PREFIX)
public class DifyProperties {

	/**
	 * Dify API 地址
	 * 例如: http://172.18.0.9:4008
	 */
	private String apiUrl;

	/**
	 * Dify API 密钥
	 * 例如: dataset-9Jj1gUdI20pf4nU1ilmQlcGg
	 */
	private String apiToken;

}
