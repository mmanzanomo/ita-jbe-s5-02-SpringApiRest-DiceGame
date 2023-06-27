package cat.itacademy.barcelonactiva.mznmon.s05.t02.auth;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.AuthenticationRequest;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.AuthenticationResponse;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.RegisterRequest;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.users.Role;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.users.User;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.jpa.IRolesJpaRepository;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.jpa.IUserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


class AuthenticationServiceTest {
    @Mock
    private IUserJpaRepository userRepository;

    @Mock
    private IRolesJpaRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_WithNonExistingEmail_ShouldSaveUserAndReturnToken() {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "password");
        Role role = new Role();
        role.setName("USER");
        User savedUser = new User();
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(role);
        String jwtToken = "generatedToken";

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(savedUser)).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser, savedUser.getId())).thenReturn(jwtToken);

        // Act
        AuthenticationResponse response = authenticationService.register(request);

        // Assert
        verify(userRepository).existsByEmail(request.email());
        verify(roleRepository).findByName("USER");
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(savedUser);
        verify(jwtService).generateToken(savedUser, savedUser.getId());
        assertEquals(jwtToken, response.token());
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "password");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act
        AuthenticationResponse response = authenticationService.register(request);

        // Assert
        verify(userRepository).existsByEmail(request.email());
        verifyNoInteractions(roleRepository, passwordEncoder, jwtService);
        assertNull(response);
    }

    @Test
    void authentication_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password");
        Role role = new Role();
        role.setName("USER");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(role);
        String jwtToken = "generatedToken";

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user, user.getId())).thenReturn(jwtToken);

        // Act
        AuthenticationResponse response = authenticationService.authentication(request);

        // Assert
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        verify(userRepository).findByEmail(request.email());
        verify(jwtService).generateToken(user, user.getId());
        assertEquals(jwtToken, response.token());
    }
}