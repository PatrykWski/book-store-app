package bookstore.service;

import bookstore.dto.user.RegisterRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.exception.RegistrationException;
import bookstore.mapper.UserMapper;
import bookstore.model.User;
import bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(
            RegisterRequestDto registerRequestDto) throws RegistrationException {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("A User with this email already exists");
        }
        User user = mapper.toModel(registerRequestDto);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        return mapper.toDto(userRepository.save(user));
    }
}
