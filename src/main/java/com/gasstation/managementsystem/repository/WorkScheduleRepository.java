package com.gasstation.managementsystem.repository;

import com.gasstation.managementsystem.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {
}
