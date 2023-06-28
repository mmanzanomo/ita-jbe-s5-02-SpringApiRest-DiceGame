package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public record GameDTO(
    @NotNull byte dice1,
    @NotNull byte dice2,
    @NotNull byte score,
    @NotNull boolean isWinner,
    @NotNull LocalDateTime playDate
) {}
