package cat.itacademy.barcelonactiva.mznmon.s05.t02.auth;


import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.AuthenticationRequest;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.AuthenticationResponse;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @Operation(summary = "Register new player")
    @ApiResponse(responseCode = "201", description = "Added player",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticationResponse.class))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Register new player")
    @ApiResponse(responseCode = "200", description = "Authenticated user",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticationResponse.class))
            })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authentication(request));
    }

}