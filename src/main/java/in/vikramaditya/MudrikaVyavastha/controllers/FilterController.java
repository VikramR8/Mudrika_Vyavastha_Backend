package in.vikramaditya.MudrikaVyavastha.controllers;

import in.vikramaditya.MudrikaVyavastha.dto.ExpenseDTO;
import in.vikramaditya.MudrikaVyavastha.dto.FilterDTO;
import in.vikramaditya.MudrikaVyavastha.dto.IncomeDTO;
import in.vikramaditya.MudrikaVyavastha.service.ExpenseService;
import in.vikramaditya.MudrikaVyavastha.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO dto) {
        LocalDate startDate = dto.getStartDate() != null ? dto.getStartDate() : LocalDate.MIN;
        LocalDate endDate = dto.getEndDate() != null ? dto.getEndDate() : LocalDate.now();
        String keyword = dto.getKeyword() != null ? dto.getKeyword(): "";
        String sortField = dto.getSortField() != null ? dto.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(dto.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        if("income".equals(dto.getType())) {
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        } else if ("expense".equalsIgnoreCase(dto.getType())) {
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenses);
        } else {
            return ResponseEntity.badRequest().body("Invalid type. Must be 'income' or 'expense'.");
        }
    }
}
