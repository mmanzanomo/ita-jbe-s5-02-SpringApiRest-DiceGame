package cat.itacademy.barcelonactiva.mznmon.s05.t02.auth;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.AuthenticationRequest;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.AuthenticationResponse;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.RegisterRequest;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.exceptions.EmailIsAlreadyExistsException;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.users.Role;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.users.User;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.jpa.IRolesJpaRepository;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.jpa.IUserJpaRepository;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.services.PlayerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final IUserJpaRepository userRepository;
    private final IRolesJpaRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);


    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        try {
            // Check if email already exists
            boolean emailExists = userRepository.existsByEmail(request.email());
            if (emailExists) throw new EmailIsAlreadyExistsException("The email is already exists");

            Optional<Role> role = roleRepository.findByName("USER");
            User user = User.builder()
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .role(role.orElseThrow())
                    .build();

            User savedUser = userRepository.save(user);
            String jwtToken = jwtService.generateToken(savedUser, savedUser.getId());
            return new AuthenticationResponse(jwtToken);
        } catch (EmailIsAlreadyExistsException e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    public AuthenticationResponse authentication(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = userRepository.findByEmail(request.email()).orElseThrow();
        var jwtToken = jwtService.generateToken(user, user.getId());
        return new AuthenticationResponse(jwtToken);
    }
}