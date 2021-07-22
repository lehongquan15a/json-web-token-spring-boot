package com.gasstation.managementsystem.controller;

import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.dto.expense.ExpenseDTO;
import com.gasstation.managementsystem.model.dto.expense.ExpenseDTOCreate;
import com.gasstation.managementsystem.model.dto.expense.ExpenseDTOUpdate;
import com.gasstation.managementsystem.service.ExpenseService;
import com.gasstation.managementsystem.utils.csv.CSVExpenseHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
@Tag(name = "Expense", description = "API for expense")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @Operation(summary = "View All expense")
    @GetMapping("/expenses")
    public HashMap<String, Object> getAll(@RequestParam(name = "pageIndex", defaultValue = "1") Integer pageIndex,
                                          @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        if (pageSize != null) {
            return expenseService.findAll(PageRequest.of(pageIndex - 1, pageSize, Sort.Direction.DESC, "id"));
        }
        return expenseService.findAll(Sort.by(Sort.Direction.DESC, "id"));

    }

    @Operation(summary = "Find expense by id")
    @GetMapping("/expenses/{id}")
    public ExpenseDTO getOne(@PathVariable(name = "id") Integer id) throws CustomNotFoundException {
        return expenseService.findById(id);
    }

    @Operation(summary = "Create new expense")
    @PostMapping("/expenses")
    public ExpenseDTO create(@Valid @RequestBody ExpenseDTOCreate expenseDTOCreate) throws CustomNotFoundException {
        return expenseService.create(expenseDTOCreate);
    }

    @Operation(summary = "Update expense by id")
    @PutMapping("/expenses/{id}")
    public ExpenseDTO update(@PathVariable(name = "id") Integer id, @Valid @RequestBody ExpenseDTOUpdate expenseDTOUpdate) throws CustomNotFoundException {
        return expenseService.update(id, expenseDTOUpdate);
    }

    @Operation(summary = "Delete expense by id")
    @DeleteMapping("/expenses/{id}")
    public ExpenseDTO delete(@PathVariable(name = "id") Integer id) throws CustomNotFoundException {
        return expenseService.delete(id);
    }

    @GetMapping("/expenses/export-csv")
    public void downloadCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; file=expenses.csv");
        HashMap<String, Object> map = expenseService.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<ExpenseDTO> expenseDTOList = map.containsKey("data") ? (List<ExpenseDTO>) map.get("data") : new ArrayList<>();
        CSVExpenseHelper.writeExpenseToCSV(response.getWriter(), expenseDTOList);
    }
}
