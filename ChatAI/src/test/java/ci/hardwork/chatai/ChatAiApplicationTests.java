package ci.hardwork.chatai;

import ci.hardwork.chatai.config.ChatAiTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(ChatAiTestConfiguration.class)
class ChatAiApplicationTests {

    @Test
    void contextLoads() {
    }

}
