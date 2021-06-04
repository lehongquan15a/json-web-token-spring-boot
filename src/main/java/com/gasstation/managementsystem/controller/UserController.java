package com.gasstation.managementsystem.controller;

import com.gasstation.managementsystem.entity.User;
import com.gasstation.managementsystem.model.dto.UserDTO;
import com.gasstation.managementsystem.service.AccountService;
import com.gasstation.managementsystem.service.UserService;
import com.gasstation.managementsystem.service.UserTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RequestMapping("/api/v1")
@RestController
@CrossOrigin
@Tag(name = "User", description = "API for User")
public class UserController {
    @Autowired
    AccountService accountService;
    @Autowired
    UserService userService;
    @Autowired
    UserTypeService userTypeService;

    @Operation(summary = "View All user")
    @GetMapping("/users")
    public HashMap<String, Object> getAll(@RequestParam(name = "pageIndex", defaultValue = "1") Integer pageIndex,
                                          @RequestParam(name = "pageSize", defaultValue = "2") Integer pageSize,
                                          @RequestParam(name = "userTypeId", required = false) Integer userTypeId) {
        if (userTypeId != null) {
            HashMap<String,Object> map = new HashMap<>();
            List<UserDTO> userDTOS =  userService.findByUserTypeId(userTypeId);
            map.put("data",userDTOS);
            return map;
        }
        return userService.findAll(PageRequest.of(pageIndex - 1, pageSize));
    }

    @Operation(summary = "Find user by id")
    @GetMapping("/users/{id}")
    public UserDTO getOne(@PathVariable(name = "id") Integer id) {
        return userService.findById(id);
    }

    @Operation(summary = "Create new user")
    @PostMapping("/users")
    public UserDTO create(@Valid @RequestBody User user) {
        return userService.save(user);
    }

    @Operation(summary = "Update user by id")
    @PutMapping("/users/{id}")
    public UserDTO update(@PathVariable(name = "id") Integer id, @Valid @RequestBody User user) {
        user.setId(id);
        return userService.save(user);
    }

    @Operation(summary = "Delete user by id")
    @DeleteMapping("/users/{id}")
    public UserDTO delete(@PathVariable(name = "id") Integer id) {
        return userService.delete(id);
    }
}
