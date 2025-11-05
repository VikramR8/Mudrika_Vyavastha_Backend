package in.vikramaditya.MudrikaVyavastha.service;

import in.vikramaditya.MudrikaVyavastha.dto.ExpenseDTO;
import in.vikramaditya.MudrikaVyavastha.entity.ProfileEntity;
import in.vikramaditya.MudrikaVyavastha.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${mudrika.vyavastha.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 21 * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job Started: sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile: profiles) {
            String body = "Hi " + profile.getFullName() + ", <br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in Mudrika Vyavastha. <br><br>"
                    + "<a href = " + frontendUrl + "style='display:inline-block; padding: 10px 20px; background-color: #4CAF50; color: #fff; text-decoration: none; border-radius:5px; font-weight:bold;'>Go to Mudrika Vyavastha</a>"
                    + "<br><br>Best Regards,<br> Mudrika Vyavastha Team";
            emailService.sendEmail(profile.getEmail(), "Daily reminder: Add your income and expenses", body);
        }
        log.info("Job Completed: sendDailyIncomeExpenseReminder()");
    }


    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void dailyExpenseSummary() {
        log.info("Job Started: sendDailyExpenseSummary()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile: profiles) {
            List<ExpenseDTO> todayExpenses = expenseService.getExpensesForUsersOnDate(profile.getId(), LocalDate.now(ZoneId.of("Asia/Kolkata")));
            if(!todayExpenses.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("<table style = 'border-collapse:collapse; width:100%;'>");
                sb.append("<tr style='background-color:#f2f2f2;'><th style='border:1px solid #ddd; padding:8px;'>S.No</th><th style='border:1px solid #ddd; padding:8px;'>Name</th><th style='border:1px solid #ddd; padding:8px;'>Category</th><th style='border:1px solid #ddd; padding:8px;'>Amount</th></tr>");
                int i= 1;
                for(ExpenseDTO expense:todayExpenses) {
                    sb.append("<tr>");
                    sb.append("<td style = 'border: 1px solid #ddd; padding:8px'>").append(i++).append("</td>");
                    sb.append("<td style = 'border: 1px solid #ddd; padding:8px'>").append(expense.getName()).append("</td>");
                    sb.append("<td style = 'border: 1px solid #ddd; padding:8px'>").append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A").append("</td>");
                    sb.append("<td style = 'border: 1px solid #ddd; padding:8px'>").append(expense.getAmount()).append("</td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                String body = "Hi " + profile.getFullName()+ ",<br><br> Here is a summary of your expenses for today: <br><br>" + sb + "<br><br>Best Regards, <br>Mudrika Vyavastha Team";
                emailService.sendEmail(profile.getEmail(), "Your daily Expense Summary", body);
            }
        }
        log.info("Job Completed: sendDailyExpenseSummary()");
    }
}
