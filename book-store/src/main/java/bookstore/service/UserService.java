package bookstore.service;

import bookstore.dto.user.RegisterRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(RegisterRequestDto registerRequestDto) throws RegistrationException;
}
