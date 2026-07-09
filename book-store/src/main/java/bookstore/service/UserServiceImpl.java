package bookstore.service;

import bookstore.dto.user.RegisterRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.exception.RegistrationException;
import bookstore.mapper.UserMapper;
import bookstore.model.User;
import bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserResponseDto register(RegisterRequestDto registerRequestDto) throws RegistrationException {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with this email already exist");
        }
        User user = new User();
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(registerRequestDto.getPassword());
        User savedUser = userRepository.save(user);
        return mapper.registerToDto(savedUser);
    }
}
