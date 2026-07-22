package bookstore.service;

import bookstore.dto.user.RegisterRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.exception.RegistrationException;
import bookstore.mapper.UserMapper;
import bookstore.model.Role;
import bookstore.model.RoleName;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.RoleRepository;
import bookstore.repository.ShoppingCartRepository;
import bookstore.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    @Transactional
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
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
        return userMapper.toDto(userRepository.save(user));
    }
}
