package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "players")
public class Player {
    @Id
    private String id;
    private Long userId;
    private String name;
    private int gamesWon;
    private int totalGames;
    private double successRate;
    private LocalDateTime registrationDate;

    @Field(name = "games")
    private List<Game> games;


    public Player() {
        registrationDate = LocalDateTime.now();
        games = new ArrayList<>();
    }

    /**
     * This method increases the number of games won.
     */
    public void setGamesWon() {
        this.gamesWon++;
    }

    /**
     * This method increases the number of plays.
     */
    public void increaseTotalGames() {
        this.totalGames++;
    }

    /**
     * This method calculates the success rate of games won.
     */
    public void setSuccessRate() {
        if (totalGames == 0) {
            successRate = (double) 0.0;
        } else {
            successRate = (double) gamesWon / totalGames * 100.0;
        }
    }

}
