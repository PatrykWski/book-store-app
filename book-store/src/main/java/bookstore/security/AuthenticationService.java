package bookstore.security;

import bookstore.dto.user.UserLoginRequestDto;
import bookstore.model.User;
import bookstore.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    public boolean authenticate(UserLoginRequestDto userLoginRequestDto) {
        Optional<User> user = userRepository.findByEmail(userLoginRequestDto.getEmail());
        return user.isPresent() && user.get().getPassword()
                .equals(userLoginRequestDto.getPassword());
    }
}
