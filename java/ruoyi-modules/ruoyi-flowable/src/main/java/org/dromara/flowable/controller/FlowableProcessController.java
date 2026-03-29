package org.dromara.flowable.controller;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.flowable.domain.bo.StartProcessBo;
import org.dromara.flowable.domain.vo.FlowProcessDefinitionVo;
import org.dromara.flowable.domain.vo.FlowTaskVo;
import org.dromara.flowable.service.IFlowableProcessService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/flowable/process")
public class FlowableProcessController {

    private final IFlowableProcessService flowableProcessService;

    @GetMapping("/definition/list")
    public TableDataInfo<FlowProcessDefinitionVo> list(@RequestParam(required = false) String category, PageQuery pageQuery) {
        return flowableProcessService.selectProcessDefinitionPage(category, pageQuery);
    }

    @GetMapping("/definition/{processDefinitionId}")
    public R<FlowProcessDefinitionVo> getInfo(@PathVariable String processDefinitionId) {
        return R.ok(flowableProcessService.selectProcessDefinitionById(processDefinitionId));
    }

    @GetMapping("/definition/xml/{processDefinitionId}")
    public void getDefinitionXml(@PathVariable String processDefinitionId, HttpServletResponse response) throws IOException {
        String xml = flowableProcessService.getProcessDefinitionXml(processDefinitionId);
        if (xml != null) {
            response.setContentType("application/xml;charset=UTF-8");
            response.getWriter().write(xml);
        }
    }

    @Log(title = "流程定义", businessType = BusinessType.IMPORT)
    @PostMapping("/definition/deploy")
    public R<String> deploy(@RequestParam("file") MultipartFile file, @RequestParam(required = false, defaultValue = "") String name) {
        if (file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(".bpmn20.xml") && !fileName.endsWith(".bpmn") && !fileName.endsWith(".zip")) {
            return R.fail("请上传 BPMN 文件");
        }
        String deployName = StrUtil.blankToDefault(name, fileName);
        String deploymentId = flowableProcessService.deployProcessDefinition(file, deployName);
        return R.ok("部署成功", deploymentId);
    }

    @Log(title = "流程定义", businessType = BusinessType.IMPORT)
    @PostMapping("/definition/deployXml")
    public R<String> deployXml(@RequestBody Map<String, String> params) {
        String xml = params.get("xml");
        String name = params.get("name");
        if (StringUtils.isBlank(xml)) {
            return R.fail("流程XML不能为空");
        }
        String deploymentId = flowableProcessService.deployProcessDefinitionFromXml(xml, name);
        return R.ok("部署成功", deploymentId);
    }

    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping("/definition/{processDefinitionId}")
    public R<Void> update(@PathVariable String processDefinitionId, @RequestParam Integer action) {
        flowableProcessService.suspendOrActivateProcessDefinition(processDefinitionId, action);
        return R.ok();
    }

    @DeleteMapping("/definition/{processDefinitionId}")
    @Log(title = "流程定义", businessType = BusinessType.DELETE)
    public R<Void> delete(@PathVariable String processDefinitionId) {
        flowableProcessService.deleteProcessDefinition(processDefinitionId);
        return R.ok();
    }

    @GetMapping("/task/todo/list")
    public TableDataInfo<FlowTaskVo> todoList(PageQuery pageQuery) {
        String userId = "admin";
        return flowableProcessService.selectTodoTaskPage(userId, pageQuery);
    }

    @GetMapping("/task/completed/list")
    public TableDataInfo<FlowTaskVo> completedList(PageQuery pageQuery) {
        String userId = "admin";
        return flowableProcessService.selectCompletedTaskPage(userId, pageQuery);
    }

    @Log(title = "流程实例", businessType = BusinessType.INSERT)
    @PostMapping("/instance/start")
    public R<String> start(@RequestBody StartProcessBo bo) {
        ProcessInstance instance = flowableProcessService.startProcessInstance(bo);
        return R.ok("流程启动成功", instance.getId());
    }

    @Log(title = "流程任务", businessType = BusinessType.UPDATE)
    @PostMapping("/task/complete")
    public R<Void> complete(@RequestParam String taskId, @RequestParam(required = false) String userId, @RequestBody(required = false) Map<String, Object> variables) {
        flowableProcessService.completeTask(taskId, userId, variables);
        return R.ok();
    }

    @Log(title = "流程任务", businessType = BusinessType.UPDATE)
    @PostMapping("/task/claim")
    public R<Void> claim(@RequestParam String taskId, @RequestParam String userId) {
        flowableProcessService.claimTask(taskId, userId);
        return R.ok();
    }

    @GetMapping("/instance/diagram/{processInstanceId}")
    public void getProcessDiagram(@PathVariable String processInstanceId, HttpServletResponse response) throws IOException {
        byte[] diagram = flowableProcessService.getProcessDiagram(processInstanceId);
        if (diagram != null) {
            response.setContentType("image/png");
            OutputStream out = response.getOutputStream();
            out.write(diagram);
            out.flush();
        }
    }
}
