package bookstore.service;

import bookstore.exception.EntityNotFoundException;
import bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MyUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws EntityNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found"));
    }
}
