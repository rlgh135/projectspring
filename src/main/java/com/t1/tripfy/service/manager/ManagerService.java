package com.t1.tripfy.service.manager;

import java.util.ArrayList;

import com.t1.tripfy.domain.dto.ManagerDTO;
import com.t1.tripfy.domain.dto.TaskMessageDTO;

public interface ManagerService {
	ManagerDTO allowManager(String managerKey);

	ArrayList<TaskMessageDTO> getTaskList(TaskMessageDTO task);

	boolean taskUpdateAnswer(TaskMessageDTO task);

	TaskMessageDTO taskRegist(TaskMessageDTO task);
}
