package ci.hardwork.authservice.config;

import ci.hardwork.authservice.core.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanExpiredTokens() {
        log.info("Running scheduled task to clean expired refresh tokens");
        refreshTokenService.deleteExpiredTokens();
    }
}