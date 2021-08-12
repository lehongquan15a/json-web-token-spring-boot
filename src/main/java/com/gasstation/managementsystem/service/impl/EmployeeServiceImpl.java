package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.Employee;
import com.gasstation.managementsystem.entity.Station;
import com.gasstation.managementsystem.exception.custom.CustomDuplicateFieldException;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.CustomError;
import com.gasstation.managementsystem.model.dto.employee.EmployeeDTO;
import com.gasstation.managementsystem.model.dto.employee.EmployeeDTOCreate;
import com.gasstation.managementsystem.model.dto.employee.EmployeeDTOUpdate;
import com.gasstation.managementsystem.model.mapper.EmployeeMapper;
import com.gasstation.managementsystem.repository.EmployeeRepository;
import com.gasstation.managementsystem.service.EmployeeService;
import com.gasstation.managementsystem.utils.OptionalValidate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final OptionalValidate optionalValidate;

    private HashMap<String, Object> listEmployeeToMap(List<Employee> employees) {
        List<EmployeeDTO> employeeDTOS = employees.stream().map(EmployeeMapper::toEmployeeDTO).collect(Collectors.toList());
        HashMap<String, Object> map = new HashMap<>();
        map.put("data", employeeDTOS);
        return map;
    }


    @Override
    public HashMap<String, Object> findAll() {
        return listEmployeeToMap(employeeRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
    }

    @Override
    public HashMap<String, Object> findAllByOwnerId(int ownerId) {
        return listEmployeeToMap(employeeRepository.findAllByOwnerId(ownerId, Sort.by(Sort.Direction.ASC, "id")));
    }

    @Override
    public EmployeeDTO findById(int id) throws CustomNotFoundException {
        return EmployeeMapper.toEmployeeDTO(optionalValidate.getEmployeeById(id));
    }

    @Override
    public EmployeeDTO create(EmployeeDTOCreate employeeDTOCreate) throws CustomDuplicateFieldException, CustomNotFoundException {
        checkDuplicate(employeeDTOCreate.getPhone(), employeeDTOCreate.getIdentityCardNumber());
        Employee employee = EmployeeMapper.toEmployee(employeeDTOCreate);
        Station station = optionalValidate.getStationById(employeeDTOCreate.getStationId());
        employee.setStation(station);
        trimString(employee);
        employee = employeeRepository.save(employee);
        return EmployeeMapper.toEmployeeDTO(employee);
    }

    private void trimString(Employee employee) {
        employee.setName(StringUtils.trim(employee.getName()));
        employee.setAddress(StringUtils.trim(employee.getAddress()));
    }

    private void checkDuplicate(String phone, String identityCardNumber) throws CustomDuplicateFieldException {
        if (phone != null) {
            Optional<Employee> employee = employeeRepository.findByPhone(phone);
            if (employee.isPresent()) {
                throw new CustomDuplicateFieldException(CustomError.builder()
                        .code("duplicate").field("phone").message("Phone is duplicate").table("employee_table").build());
            }
        }
        if (identityCardNumber != null) {
            Optional<Employee> employee = employeeRepository.findByIdentityCardNumber(identityCardNumber);
            if (employee.isPresent()) {
                throw new CustomDuplicateFieldException(CustomError.builder().code("duplicate")
                        .field("identityCardNumber").message("Duplicate field").build());
            }
        }

    }

    @Override
    public EmployeeDTO update(int id, EmployeeDTOUpdate employeeDTOUpdate) throws CustomNotFoundException, CustomDuplicateFieldException {
        Employee oldEmployee = optionalValidate.getEmployeeById(id);
        String phone = employeeDTOUpdate.getPhone();
        String identityCardNumber = employeeDTOUpdate.getIdentityCardNumber();
        if (phone != null && phone.equalsIgnoreCase(oldEmployee.getPhone())) {
            phone = null;
        }
        if (identityCardNumber != null && identityCardNumber.equalsIgnoreCase(oldEmployee.getIdentityCardNumber())) {
            identityCardNumber = null;
        }
        checkDuplicate(phone, identityCardNumber);
        EmployeeMapper.copyNonNullToEmployee(oldEmployee, employeeDTOUpdate);
        trimString(oldEmployee);
        oldEmployee = employeeRepository.save(oldEmployee);
        return EmployeeMapper.toEmployeeDTO(oldEmployee);
    }

    @Override
    public EmployeeDTO delete(int id) throws CustomNotFoundException {
        Employee employee = optionalValidate.getEmployeeById(id);
        employeeRepository.delete(employee);
        return EmployeeMapper.toEmployeeDTO(employee);
    }

}
