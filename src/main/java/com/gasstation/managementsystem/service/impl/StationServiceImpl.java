package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.Station;
import com.gasstation.managementsystem.entity.User;
import com.gasstation.managementsystem.entity.UserType;
import com.gasstation.managementsystem.exception.custom.CustomBadRequestException;
import com.gasstation.managementsystem.exception.custom.CustomDuplicateFieldException;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.CustomError;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.station.StationDTOCreate;
import com.gasstation.managementsystem.model.dto.station.StationDTOUpdate;
import com.gasstation.managementsystem.model.mapper.StationMapper;
import com.gasstation.managementsystem.repository.StationRepository;
import com.gasstation.managementsystem.repository.UserRepository;
import com.gasstation.managementsystem.service.StationService;
import com.gasstation.managementsystem.utils.OptionalValidate;
import com.gasstation.managementsystem.utils.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final OptionalValidate optionalValidate;
    private final UserHelper userHelper;

    private HashMap<String, Object> listStationToMap(List<Station> stations) {
        List<StationDTO> stationDTOS = new ArrayList<>();
        for (Station station : stations) {
            stationDTOS.add(StationMapper.toStationDTO(station));
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("data", stationDTOS);
        return map;
    }

    @Override
    public HashMap<String, Object> findAll() {
        User userLoggedIn = userHelper.getUserLogin();
        List<Station> stations = new ArrayList<>();
        int userTypeId = userLoggedIn.getUserType().getId();
        switch (userTypeId) {
            case UserType.ADMIN:
                stations = stationRepository.findAll();
                break;
            case UserType.OWNER:
                stations = stationRepository.findByOwnerId(userLoggedIn.getId());
                break;
        }
        return listStationToMap(stations);
    }

    @Override
    public StationDTO findById(int id) throws CustomNotFoundException {
        User userLoggedIn = userHelper.getUserLogin();
        UserType userType = userLoggedIn.getUserType();
        Station station = optionalValidate.getStationById(id);
        if (userType.getId() == UserType.OWNER && !station.getOwner().getId().equals(userLoggedIn.getId())) {//id không phải của nó
            throw new CustomNotFoundException(CustomError.builder()
                    .code("not.found")
                    .message("Station not of the owner")
                    .table("station_tbl").build());
        }
        return StationMapper.toStationDTO(station);
    }

    private User validateOwner(int id) throws CustomBadRequestException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) throw new CustomBadRequestException(CustomError.builder()
                .code("not.exist").field("id").message("owner is not exist").build());
        User owner = optionalUser.get();
        if (owner.getUserType().getId() != UserType.OWNER) {
            throw new CustomBadRequestException(CustomError.builder()
                    .code("not.match.type").field("type_id").message("user not type owner").build());
        }
        return optionalUser.get();
    }

    @Override
    public StationDTO create(StationDTOCreate stationDTOCreate) throws CustomBadRequestException, CustomDuplicateFieldException {
        checkDuplicate(stationDTOCreate.getName(), stationDTOCreate.getAddress());
        Station station = StationMapper.toStation(stationDTOCreate);
        User owner = validateOwner(stationDTOCreate.getOwnerId());
        station.setOwner(owner);
        station = stationRepository.save(station);
        return StationMapper.toStationDTO(station);
    }

    private void checkDuplicate(String name, String address) throws CustomDuplicateFieldException {
        if (name != null && address != null) {
            Optional<Station> stationOptional = stationRepository.findByNameAndAddress(name, address);
            if (stationOptional.isPresent()) {
                throw new CustomDuplicateFieldException(CustomError.builder()
                        .code("duplicate").field("(name,address)").message("Duplicate field (name,address)")
                        .table("station_tbl, error in StationServiceImpl.class").build());
            }
        }
    }

    @Override
    public StationDTO update(int id, StationDTOUpdate stationDTOUpdate) throws CustomBadRequestException, CustomNotFoundException, CustomDuplicateFieldException {
        Station oldStation = optionalValidate.getStationById(id);
        String name = stationDTOUpdate.getName();
        String address = stationDTOUpdate.getAddress();
        if (notChangeNameAndAddress(name, address, oldStation)) {
            name = null;
            address = null;
        }
        checkDuplicate(name, address);
        StationMapper.copyNonNullToStation(oldStation, stationDTOUpdate);
        if (stationDTOUpdate.getOwnerId() != null) {
            User owner = validateOwner(stationDTOUpdate.getOwnerId());
            oldStation.setOwner(owner);
        }
        oldStation = stationRepository.save(oldStation);
        return StationMapper.toStationDTO(oldStation);
    }

    private boolean notChangeNameAndAddress(String name, String address, Station oldStation) {
        return name != null && name.equalsIgnoreCase(oldStation.getName())
                && address != null && address.equalsIgnoreCase(oldStation.getAddress());
    }

    @Override
    public StationDTO delete(int id) throws CustomNotFoundException {
        Station station = optionalValidate.getStationById(id);
        stationRepository.delete(station);
        return StationMapper.toStationDTO(station);
    }
}
