package com.t1.tripfy.service.manager;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.t1.tripfy.domain.dto.ManagerDTO;
import com.t1.tripfy.domain.dto.TaskMessageDTO;
import com.t1.tripfy.mapper.manager.ManagerMapper;

@Service
public class ManagerServiceImpl implements ManagerService{

	@Autowired
	private ManagerMapper mmapper;
	
	@Override
	public ManagerDTO allowManager(String managerKey) {
		ManagerDTO manager = mmapper.checkKey(managerKey);
		return manager;
	}

	@Override
	public ArrayList<TaskMessageDTO> getTaskList(TaskMessageDTO task) {
		return mmapper.getTaskMessageList(task);
	}

	@Override
	public boolean taskUpdateAnswer(TaskMessageDTO task) {
		return mmapper.updateAnswer(task) == 1;
	}

	@Override
	public TaskMessageDTO taskRegist(TaskMessageDTO task) {
		if(mmapper.insertTask(task) == 1) {
			return mmapper.getLastTaskByUserid(task.getSendid());
		}else {			
			return null;
		}
	}
}
