package ci.hardwork.chatai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ChatAiTestConfiguration {

    @Bean
    @Primary
    public ChatModel testChatModel() {
        return mock(ChatModel.class);
    }

    @Bean
    @Primary
    public RestTemplate testRestTemplate() {
        return mock(RestTemplate.class);
    }
}