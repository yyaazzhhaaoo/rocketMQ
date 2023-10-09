package com.myz.rocketmq.provider.service.impl;

import com.myz.rocketmq.provider.mapper.TaskMapper;
import com.myz.rocketmq.provider.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public Object list() {
        return taskMapper.list();
    }
}
