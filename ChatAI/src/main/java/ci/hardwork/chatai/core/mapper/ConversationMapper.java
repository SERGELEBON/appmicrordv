package ci.hardwork.chatai.core.mapper;

import ci.hardwork.chatai.core.dto.ConversationDto;
import ci.hardwork.chatai.core.models.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    
    @Mapping(target = "messageCount", expression = "java(conversation.getMessageCount())")
    @Mapping(target = "recentMessages", ignore = true)
    ConversationDto toDto(Conversation conversation);
    
    List<ConversationDto> toDto(List<Conversation> conversations);
}