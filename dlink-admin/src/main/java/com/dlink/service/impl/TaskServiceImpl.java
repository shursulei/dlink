package com.dlink.service.impl;

import com.dlink.assertion.Assert;
import com.dlink.cluster.FlinkCluster;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.exception.BusException;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.job.JobManager;
import com.dlink.mapper.TaskMapper;
import com.dlink.model.Cluster;
import com.dlink.model.Statement;
import com.dlink.model.Task;
import com.dlink.result.SubmitResult;
import com.dlink.service.ClusterService;
import com.dlink.service.StatementService;
import com.dlink.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务 服务实现类
 *
 * @author wenmo
 * @since 2021-05-24
 */
@Service
public class TaskServiceImpl extends SuperServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private StatementService statementService;
    @Autowired
    private ClusterService clusterService;

    @Override
    public SubmitResult submitByTaskId(Integer id) {
        Task task = this.getById(id);
        Assert.check(task);
        Cluster cluster = clusterService.getById(task.getClusterId());
        Assert.check(cluster);
        Statement statement = statementService.getById(id);
        Assert.check(statement);
        String host = FlinkCluster.testFlinkJobManagerIP(cluster.getHosts(), cluster.getJobManagerHost());
        Assert.checkHost(host);
        JobManager jobManager = new JobManager(host);
        return jobManager.submit(statement.getStatement(), task.getRemoteExecutorSetting());
    }

}