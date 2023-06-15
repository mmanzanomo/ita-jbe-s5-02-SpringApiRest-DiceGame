package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos;

import lombok.Data;

@Data
public class PlayerRateDTO {
    private String id;
    private String name;
    private int gamesWon;
    private int totalGames;
    private double successRate;
}
