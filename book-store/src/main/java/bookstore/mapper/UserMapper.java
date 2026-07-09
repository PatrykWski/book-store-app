package bookstore.mapper;

import bookstore.dto.user.UserResponseDto;
import bookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto registerToDto(User user);
}
