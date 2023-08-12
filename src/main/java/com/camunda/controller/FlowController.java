package cn.xg.process.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.xg.bfs.foundation.rpc.ApiResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

@RestController
@RequestMapping("/flow")
public class FlowController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @PostMapping("/deploy")
    public ApiResult<String> deploy(@RequestBody FlowReq req) {
        Deployment deploy = repositoryService.createDeployment()
            .name(req.name) // 定义部署文件的名称
            .addClasspathResource("bpmn/" + req.bpmnName) // 绑定需要部署的流程文件
            .deploy();
        return ApiResult.success(deploy.getId() + " : " + deploy.getName());
    }

    @PostMapping("/start")
    public ApiResult<String> start(@RequestBody FlowReq req) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(req.processInstanceId);
        return ApiResult.success(processInstance.getId() + " : " + processInstance.getProcessDefinitionId());
    }

    /**
     * processDefinitionId:流程定义Id，部署流程时会产生
     * processInstanceId:流程实例Id，启动流程则会产生
     *
     * @return
     */
    @PostMapping("/query")
    public ApiResult<List<Task>> query() {
        List<Task> list = taskService.createTaskQuery().list();
        return ApiResult.success(list);
    }

    @PostMapping("/complete")
    public ApiResult<String> complete() {
        List<Task> tasks = taskService.createTaskQuery().list();
        tasks.forEach(task -> taskService.complete(task.getId()));
        return ApiResult.success("任务审批完成···");
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class FlowReq {
        private String name;
        private String bpmnName;
        private String processInstanceId;
    }
}
