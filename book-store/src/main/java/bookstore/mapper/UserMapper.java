package bookstore.mapper;

import bookstore.dto.user.RegisterRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(RegisterRequestDto registerRequestDto);
}
