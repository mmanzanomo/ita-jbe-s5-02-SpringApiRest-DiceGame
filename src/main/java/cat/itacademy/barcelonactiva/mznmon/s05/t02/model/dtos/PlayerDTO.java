package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerDTO {
    private String id;
    private Long userId;
    private String name;
    private int gamesWon;
    private int totalGames;
    private double successRate;
    private List<GameDTO> games;

    public PlayerDTO() {
        games = new ArrayList<>();
    }
}
