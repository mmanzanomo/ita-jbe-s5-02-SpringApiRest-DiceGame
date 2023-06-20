package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos;


import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlayerDTO(
    @NotNull String id,
    @NotNull Long userId,
    @NotNull String name,
    @NotNull int gamesWon,
    @NotNull int totalGames,
    @NotNull double successRate,
    List<GameDTO> games
) {}
