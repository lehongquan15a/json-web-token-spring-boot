package com.gasstation.managementsystem.service;

import com.gasstation.managementsystem.exception.custom.CustomBadRequestException;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.dto.workSchedule.WorkScheduleDTO;
import com.gasstation.managementsystem.model.dto.workSchedule.WorkScheduleDTOCreate;
import com.gasstation.managementsystem.model.dto.workSchedule.WorkScheduleDTOUpdate;

import java.util.HashMap;

public interface WorkScheduleService {
    HashMap<String, Object> findAll();

    WorkScheduleDTO findById(int id) throws CustomNotFoundException;

    WorkScheduleDTO create(WorkScheduleDTOCreate workScheduleDTOCreate) throws CustomNotFoundException, CustomBadRequestException;

    WorkScheduleDTO update(int id, WorkScheduleDTOUpdate workScheduleDTOUpdate) throws CustomNotFoundException, CustomBadRequestException;

    WorkScheduleDTO delete(int id) throws CustomNotFoundException;
}
