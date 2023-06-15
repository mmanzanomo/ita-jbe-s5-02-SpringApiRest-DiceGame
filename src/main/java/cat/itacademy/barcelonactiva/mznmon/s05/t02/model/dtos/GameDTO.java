package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameDTO {
    private Long id;
    private byte dice1;
    private byte dice2;
    private byte score;
    private boolean isWinner;
    private LocalDateTime playDate;

}
