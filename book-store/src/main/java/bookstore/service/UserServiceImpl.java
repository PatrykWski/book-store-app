package bookstore.service;

import bookstore.dto.user.RegisterRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.exception.RegistrationException;
import bookstore.mapper.UserMapper;
import bookstore.model.Role;
import bookstore.model.RoleName;
import bookstore.model.User;
import bookstore.repository.RoleRepository;
import bookstore.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(
            RegisterRequestDto registerRequestDto) throws RegistrationException {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("A user with this email already exist");
        }
        User user = userMapper.toModel(registerRequestDto);
        Role role = roleRepository.findByName(RoleName.USER).orElseThrow(
                () -> new EntityNotFoundException("USER role not found"));
        user.setRoles(Set.of(role));
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }
}
