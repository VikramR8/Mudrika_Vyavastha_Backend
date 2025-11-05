package in.vikramaditya.MudrikaVyavastha.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.from.email}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmail(String to, String subject, String body) {
        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", Map.of("email", fromEmail));
            requestBody.put("to", new Object[]{Map.of("email", to)});
            requestBody.put("subject", subject);
            requestBody.put("htmlContent", "<html><body>" + body + "</body></html>");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            System.out.println("✅ Email sent successfully to " + to);

        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
