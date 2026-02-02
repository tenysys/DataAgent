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
package com.alibaba.cloud.ai.dataagent.config;

import com.alibaba.cloud.ai.dataagent.properties.FileStorageProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Web配置类
 */
@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final FileStorageProperties fileStorageProperties;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String uploadDir = Paths.get(fileStorageProperties.getPath()).toAbsolutePath().toString();

		registry.addResourceHandler(fileStorageProperties.getUrlPrefix() + "/**")
			.addResourceLocations("file:" + uploadDir + "/")
			.setCachePeriod(3600);
		// Spring Boot 3.x的资源处理器配置
		registry.addResourceHandler("/**")
				.addResourceLocations(
						"classpath:/META-INF/resources/",
						"classpath:/resources/",
						"classpath:/static/",
						"classpath:/public/"
				)
				.setCachePeriod(3600)
				.resourceChain(true)
				.addResolver(new PathResourceResolver() {
					@Override
					protected Resource getResource(String resourcePath, Resource location) throws IOException {
						Resource requestedResource = location.createRelative(resourcePath);
						// 如果请求的资源存在
						if (requestedResource.exists() && requestedResource.isReadable()) {
							return requestedResource;
						}
						// 对于API请求，不重定向
						if (resourcePath.startsWith("api/") ||
								resourcePath.startsWith("actuator/") ||
								resourcePath.contains(".")) {
							return null;
						}
						// 否则返回index.html（SPA支持）
						return new ClassPathResource("/static/index.html");
					}
				});
	}

}
