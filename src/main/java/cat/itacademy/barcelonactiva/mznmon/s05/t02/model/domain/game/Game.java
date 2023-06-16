package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.game;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.game.Dice;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "games")
public class Game {
    @Id
    private Long id;
    private byte dice1;
    private byte dice2;
    private byte score;
    private boolean isWinner;
    private LocalDateTime playDate;


    public Game() {
        playDate = LocalDateTime.now();
        dice1 = (byte) Dice.getInstance().Roll();
        dice2 = (byte) Dice.getInstance().Roll();
        setPoints();
        calculateIfWinner();
    }

    /**
     * This method set the sum of the two dice.
     */
    public void setPoints() {
        score = (byte) (dice1 + dice2);
    }

    /**
     * This method calculates if the player has won.
     */
    public void calculateIfWinner() {
        isWinner = getScore() == 7;
    }

}
