package cat.itacademy.barcelonactiva.mznmon.s05.t02.auth;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    @Mock
    private Key mockSignInKey;

    @InjectMocks
    private JwtService jwtService;

    private String secretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        secretKey = "876696e0ab00a1069c1ef33dedd4c0b563543e857d98c0fb42d826b37b866f5c";
        mockSignInKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        ReflectionTestUtils.setField(jwtService, "time_expiration", "3600000");
        ReflectionTestUtils.setField(jwtService, "Secret_key", secretKey);
    }

    @Test
    void testGenerateToken() {
        // Given
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);

        Long userId = 1L;

        Map<String, Object> extraClaims = new HashMap<>();

        // When
        String token = jwtService.generateToken(extraClaims, userDetails, userId);

        // Then
        assertNotNull(token);
    }

}