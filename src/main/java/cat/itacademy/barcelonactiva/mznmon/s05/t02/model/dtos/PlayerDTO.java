package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos;


import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlayerDTO(
    String id,
    Long userId,
    String name,
    @NotNull int gamesWon,
    @NotNull int totalGames,
    @NotNull double successRate,
    List<GameDTO> games
) {}
