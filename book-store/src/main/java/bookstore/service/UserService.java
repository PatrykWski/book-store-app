package bookstore.service;

import bookstore.dto.user.RegisterRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.exception.RegistrationException;
import jakarta.validation.Valid;

public interface UserService {
    UserResponseDto register(@Valid RegisterRequestDto registerRequestDto) throws RegistrationException;
}
