package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos;

import lombok.Data;

@Data
public class RankingDTO {
    private static RankingDTO instance;
    private double mean;

    private RankingDTO() {
        setMean(mean);
    }

    public static RankingDTO getInstance(double mean) {
        if (instance == null) {
            instance = new RankingDTO();
        }
        instance.setMean(mean);
        return instance;
    }

}