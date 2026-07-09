package bookstore.mapper;

import bookstore.dto.user.UserDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(UserDto userDto);

    UserResponseDto registerToDto(User user);
}
