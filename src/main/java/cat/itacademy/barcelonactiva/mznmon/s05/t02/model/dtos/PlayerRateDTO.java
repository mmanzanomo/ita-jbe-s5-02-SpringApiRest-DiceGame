package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos;


import jakarta.validation.constraints.NotNull;

public record PlayerRateDTO(
    @NotNull String id,
    @NotNull String name,
    @NotNull int gamesWon,
    @NotNull int totalGames,
    @NotNull double successRate
) {}
