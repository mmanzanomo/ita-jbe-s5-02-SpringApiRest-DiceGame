package cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(@NotNull @Email @NotBlank String email, @NotNull @NotBlank String password) {}
