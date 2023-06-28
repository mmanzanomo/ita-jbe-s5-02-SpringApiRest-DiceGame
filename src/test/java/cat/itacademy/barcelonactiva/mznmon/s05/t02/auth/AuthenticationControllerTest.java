package cat.itacademy.barcelonactiva.mznmon.s05.t02.auth;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.AuthenticationRequest;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.AuthenticationResponse;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.RegisterRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {
    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void register() {
        // Given
        RegisterRequest request = new RegisterRequest("testUser@mail.mail", "passtest001");
        AuthenticationResponse expectedResponse = new AuthenticationResponse("onesupertoken");

        // When
        when(authenticationService.register(request)).thenReturn(expectedResponse);

        // Call method to register a new user
        ResponseEntity<?> response = authenticationController.register(request);
        // Verify the response
        verify(authenticationService).register(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void authentication() {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("testUser@mail.mail", "passtest001");
        AuthenticationResponse expectedResponse = new AuthenticationResponse("onesupertoken");

        // When
        when(authenticationService.authentication(request)).thenReturn(expectedResponse);

        // Call controller method
        ResponseEntity<?> response = authenticationController.authentication(request);

        verify(authenticationService).authentication(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
}