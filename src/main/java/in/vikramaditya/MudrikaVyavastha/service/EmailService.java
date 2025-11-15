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


    public void sendEmail(String to, String subject, String bodyHtml) {
        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", Map.of("email", fromEmail));
            requestBody.put("to", new Object[]{Map.of("email", to)});
            requestBody.put("subject", subject);

            requestBody.put("htmlContent", bodyHtml);

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
    public void sendVerificationEmail(String to, String userName, String verificationLink) {

        String htmlContent =
                "<div style='font-family:Arial, sans-serif; max-width:600px; margin:20px auto; "
                        + "padding:20px; border:1px solid #eaeaea; border-radius:10px;'>"
                        + "<h2 style='color:#0d6efd; text-align:center;'>Verify your email</h2>"
                        + "<p style='font-size:14px;'>Hi " + userName + ", please confirm your email to continue using Mudrika Vyavastha.</p>"
                        + "<p style='text-align:center; margin:20px 0;'>"
                        + "<a href='" + verificationLink + "' style='display:inline-block; padding:10px 16px; background:#0d6efd; "
                        + "color:white; border-radius:6px; text-decoration:none;'>Verify Email</a>"
                        + "</p>"
                        + "<p style='font-size:12px; color:#777; text-align:center;'>This link expires in 24 hours.</p>"
                        + "</div>";

        sendEmail(to, "Verify your email", htmlContent);
    }
}
