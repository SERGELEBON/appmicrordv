package ci.hardwork.chatai.core.mapper;

import ci.hardwork.chatai.core.dto.MessageDto;
import ci.hardwork.chatai.core.models.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    
    @Mapping(target = "conversationId", source = "conversation.id")
    MessageDto toDto(Message message);
    
    List<MessageDto> toDto(List<Message> messages);
}