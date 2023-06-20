package cat.itacademy.barcelonactiva.mznmon.s05.t02.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(String name, @Email @NotBlank String email, @NotBlank String password) {}
