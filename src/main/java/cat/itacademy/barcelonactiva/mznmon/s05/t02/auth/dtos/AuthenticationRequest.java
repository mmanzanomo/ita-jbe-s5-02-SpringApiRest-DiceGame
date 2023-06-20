package cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(@Email @NotBlank String email, @NotBlank String password) {}
