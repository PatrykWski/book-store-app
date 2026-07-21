package bookstore.security;

import bookstore.dto.user.UserLoginRequestDto;
import bookstore.dto.user.UserLoginResponseDto;
import bookstore.exception.LoginException;
import bookstore.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto userLoginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginRequestDto.getEmail(),
                            userLoginRequestDto.getPassword()
                    )
            );
            User user = (User) authentication.getPrincipal();
            String jwtToken = jwtUtil.generateToken(user);
            return new UserLoginResponseDto(jwtToken);
        } catch (AuthenticationException ex) {
            throw new LoginException("Invalid email or password");
        }
    }
}
