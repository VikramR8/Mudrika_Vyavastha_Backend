package in.vikramaditya.MudrikaVyavastha.controllers;

import in.vikramaditya.MudrikaVyavastha.entity.ProfileEntity;
import in.vikramaditya.MudrikaVyavastha.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/email")
public class EmailController {

        private final ExcelService excelService;
        private final IncomeService incomeService;
        private final ExpenseService expenseService;
        private final EmailService emailService;
        private final ProfileService profileService;

        @GetMapping("/income-excel")
        public ResponseEntity<Void> emailIncomeExcel() throws IOException, MessagingException {
            ProfileEntity profile = profileService.getCurrentProfile();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            excelService.writeIncomesToExcel(bs, incomeService.getCurrentMonthIncomesForCurrentUser());
            emailService.sendEmailWithAttachment(profile.getEmail(),
                    "Your Income Excel Report",
                    "Please find attached your income report",
                    bs.toByteArray(),
                    "Incomes.xlsx");
            return ResponseEntity.ok(null);

        }

    @GetMapping("/expense-excel")
    public ResponseEntity<Void> emailExpenseExcel() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        excelService.writeExpenseToExcel(bs, expenseService.getCurrentMonthExpensesForCurrentUser());
        emailService.sendEmailWithAttachment(profile.getEmail(),
                "Your Expense Excel Report",
                "Please find attached your expense report",
                bs.toByteArray(),
                "Expenses.xlsx");
        return ResponseEntity.ok(null);

    }
}
