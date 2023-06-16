package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.game;

import java.util.Random;

/**
 * This class follows a singleton pattern that is called via its getInstance() method.
 */
public class Dice {
    private static Dice instance;
    Random random;

    private Dice() {
        random = new Random();
    }

    /**
     * This method creates an instance of Dice. If an instance is already exists, get its reference.
     * @return an instance of Dice.
     */
    public static Dice getInstance() {
        if (instance == null) {
            instance = new Dice();
        }
        return instance;
    }

    /**
     * This method generates a random value between 1 and 6.
     * @return a value between 1 and 6.
     */
    public int Roll() {
        return random.nextInt(6) + 1;
    }
}
