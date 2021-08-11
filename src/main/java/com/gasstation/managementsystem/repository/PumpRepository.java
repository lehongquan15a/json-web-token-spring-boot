package com.gasstation.managementsystem.repository;

import com.gasstation.managementsystem.entity.Pump;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PumpRepository extends JpaRepository<Pump, Integer> {

    Optional<Pump> findByNameAndTankId(String name, int tankId);

    @Query("select p from Pump p inner join p.tank t where t.station.id in (?1)")
    List<Pump> findAllByStationIds(List stationIds, Sort sort);
}
