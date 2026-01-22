package in.vikramaditya.MudrikaVyavastha.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
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

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";
    public void sendEmail(String to, String subject, String bodyHtml) {

        try {
            String finalHtml = wrapHtml(bodyHtml);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", Map.of("email", fromEmail));
            requestBody.put("to", new Object[]{ Map.of("email", to) });
            requestBody.put("subject", subject);
            requestBody.put("htmlContent", finalHtml);

            HttpHeaders headers = buildHeaders();
            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(requestBody, headers);

            restTemplate.exchange(
                    BREVO_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Verification email
     */
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

    /**
     * Email with attachment (Excel, PDF, etc.)
     */
    public void sendEmailWithAttachment(
            String to,
            String subject,
            String bodyHtml,
            byte[] attachment,
            String fileName
    ) {

        try {
            String finalHtml = wrapHtml(bodyHtml);
            String base64File = Base64.getEncoder().encodeToString(attachment);

            Map<String, Object> attachmentObj = new HashMap<>();
            attachmentObj.put("content", base64File);
            attachmentObj.put("name", fileName);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", Map.of("email", fromEmail));
            requestBody.put("to", new Object[]{ Map.of("email", to) });
            requestBody.put("subject", subject);
            requestBody.put("htmlContent", finalHtml);
            requestBody.put("attachment", new Object[]{ attachmentObj });

            HttpHeaders headers = buildHeaders();
            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(requestBody, headers);

            restTemplate.exchange(
                    BREVO_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);
        return headers;
    }

    private String wrapHtml(String bodyHtml) {
        return "<!DOCTYPE html>"
                + "<html><head><meta charset=\"utf-8\"></head><body>"
                + bodyHtml
                + "</body></html>";
    }
}
