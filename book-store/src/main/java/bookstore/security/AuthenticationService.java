package bookstore.security;

import bookstore.dto.user.UserLoginRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.exception.LoginException;
import bookstore.mapper.UserMapper;
import bookstore.model.User;
import bookstore.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponseDto authenticate(UserLoginRequestDto userLoginRequestDto) {
        Optional<User> user = userRepository.findByEmail(userLoginRequestDto.getEmail());
        if (user.isEmpty()) {
            throw new EntityNotFoundException("A user with this email doesn't exist");
        }
        if (passwordEncoder.matches(userLoginRequestDto.getPassword(),
                user.get().getPassword())) {
            return user.stream()
                    .map(userMapper::toDto)
                    .findAny()
                    .get();
        }
        throw new LoginException("Incorrect login or password");
    }
}
