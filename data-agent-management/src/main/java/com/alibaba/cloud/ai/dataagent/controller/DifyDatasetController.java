
package com.alibaba.cloud.ai.dataagent.controller;

import com.alibaba.cloud.ai.dataagent.dto.dify.AgentDatasetDto;
import com.alibaba.cloud.ai.dataagent.dto.dify.DifyDatasetDto;
import com.alibaba.cloud.ai.dataagent.qo.AddDatasetQo;
import com.alibaba.cloud.ai.dataagent.qo.AgentIdsQueryQo;
import com.alibaba.cloud.ai.dataagent.qo.DifyPageQueryQo;
import com.alibaba.cloud.ai.dataagent.service.dify.DifyAgentKnowledgeService;
import com.nrec.base.common.model.Result;
import com.nrec.base.common.model.TablePage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dify Dataset Controller
 * Dify 知识库管理控制器
 *
 * @author shihb
 */
@Slf4j
@RestController
@RequestMapping("/api/dify/dataset")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class DifyDatasetController {

	private final DifyAgentKnowledgeService difyAgentKnowledgeService;

	/**
	 * 分页获取 Dify 知识库列表
	 *
	 * @param query 查询参数
	 * @return 知识库列表分页响应
	 */
	@PostMapping("/page")
	public Result<TablePage<DifyDatasetDto>> getDatasetPage(@Valid @RequestBody DifyPageQueryQo query) {
		try {
			List<DifyDatasetDto> data = difyAgentKnowledgeService.getDatasetPage(query);
			Integer total = difyAgentKnowledgeService.getDatasetTotal(query);
			return Result.buildSuccess(new TablePage<>(data, total), "查询成功");
		}
		catch (Exception e) {
			log.error("分页查询知识库列表失败：{}", e.getMessage(), e);
			return Result.buildFailed("分页查询失败：" + e.getMessage());
		}
	}

	/**
	 * 添加知识库与智能体绑定
	 *
	 * @param request 添加知识库请求
	 * @return 操作结果
	 */
	@PostMapping("/insert")
	public Result<Boolean> addKnowledge(@Valid @RequestBody AddDatasetQo request) {
		try {
			Boolean result = difyAgentKnowledgeService.addKnowledge(request);
			return Result.buildSuccess(result, "添加知识库成功");
		}
		catch (Exception e) {
			log.error("添加知识库失败：{}", e.getMessage(), e);
			return Result.buildFailed("添加知识库失败：" + e.getMessage());
		}
	}

	/**
	 * 批量删除知识库绑定
	 *
	 * @param agentIdQueryQo 格式为 "agentId:datasetId"
	 * @return 操作结果
	 */
	@PostMapping("/delete")
	public Result<Boolean> deleteKnowledge(@RequestBody String agentIdQueryQo) {
		try {
			Boolean result = difyAgentKnowledgeService.deleteKnowledge(agentIdQueryQo);
			return Result.buildSuccess(result, "删除知识库成功");
		}
		catch (Exception e) {
			log.error("删除知识库失败：{}", e.getMessage(), e);
			return Result.buildFailed("删除知识库失败：" + e.getMessage());
		}
	}

	/**
	 * 查询指定智能体绑定的知识库列表
	 *
	 * @param query 智能体ID列表查询请求
	 * @return 知识库绑定列表
	 */
	@PostMapping("/getByAgent")
	public Result<List<AgentDatasetDto>> getByAgent(@Valid @RequestBody AgentIdsQueryQo query) {
		try {
			List<AgentDatasetDto> result = difyAgentKnowledgeService.getByAgent(query);
			return Result.buildSuccess(result,"查询成功");
		}
		catch (Exception e) {
			log.error("查询智能体知识库失败：{}", e.getMessage(), e);
			return Result.buildFailed("查询智能体知识库失败：" + e.getMessage());
		}
	}

}
