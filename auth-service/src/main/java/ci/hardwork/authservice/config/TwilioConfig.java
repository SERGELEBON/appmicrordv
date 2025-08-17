package ci.hardwork.authservice.config;

import com.twilio.Twilio;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@Slf4j
@Getter
public class TwilioConfig {

    @Value("${app.twilio.account-sid}")
    private String accountSid;

    @Value("${app.twilio.auth-token}")
    private String authToken;

    @Value("${app.twilio.phone-number}")
    private String phoneNumber;

    @Value("${app.twilio.sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${app.twilio.sendgrid.from-email}")
    private String fromEmail;

    @Value("${app.twilio.sendgrid.from-name}")
    private String fromName;

    @PostConstruct
    public void initTwilio() {
        try {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully with Account SID: {}", 
                    accountSid.substring(0, 8) + "..." + accountSid.substring(accountSid.length() - 4));
        } catch (Exception e) {
            log.error("Failed to initialize Twilio: {}", e.getMessage());
        }
    }
}