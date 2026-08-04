package bookstore.service;

import bookstore.exception.EntityNotFoundException;
import bookstore.model.Role;
import bookstore.model.RoleName;
import bookstore.model.User;
import bookstore.repository.RoleRepository;
import bookstore.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInitializerService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public boolean initializeUser(String email, String rawPassword, String firstName, String lastName) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFirstName(firstName);
        user.setLastName(lastName);

        List<Role> roles = roleRepository.findAll();

        user.setRoles(new HashSet<>(roles));
        user.setShippingAddress("Kowalskiego 12/12");

        userRepository.save(user);
        return true;
    }
}
